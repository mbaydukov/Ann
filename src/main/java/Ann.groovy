import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
/**
 * Created by ann on 6/4/17.
 */
class Ann {

    private static String INITIAL_URL = 'https://www.amazon.com/Levis-Mens-Original-Stonewash-34x32/dp/B0006MZHS2'
    private static def SIZE_PARAMETERS = ["th=1", "pcs=1"]
    private static def PRICE_PARAMETERS = ["psc=1"]
    private static ExecutorService es = Executors.newFixedThreadPool(10)
    private static AtomicInteger productCounter = new AtomicInteger(1)
    private static Map<String, String> headers = [
            "Accept"          : "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Encoding" : "gzip, deflate, sdch, br",
            "Accept-Language" : "en-US,en;q=0.8",
            "Cache-Control"   : "no-cache",
            "Connection"      : "keep-alive",
            "Cookie"          : "csm-sid=009-2606811-1270172; x-wl-uid=1fxIXHdkdp+ugn7IOgkM72/tBZQugaq5Dmh8PHWVmoLDMxt9+St6mxVMoKcI1B1W+XGlfkUYqISY=; session-token=\"JoOnzylGIkMSyJ4YP54zaioJz0zFRKoXLOfnMqe624DeRus64nTw1g1prONeFvT+jJw6pq5mFgS15FvQ841X00cKnGaLawlqtC5MQwvAUO/iT42NW+6PbAv7oHH4quIGp0TAKChut8rQ03bPKG4s3A/VG/HYK3SMVf9Z7OiZQ/aLlDBbgkmtzwJRxe9+3paIZDl+aj/zedOLulyDvgqgyRWXWx2Fwmv6YnHoeu2Lvlc4eaes5dS/we8kTKMFxi2wUy9hZW9oFWg=\"; x-amz-captcha-1=1497029256144904; x-amz-captcha-2=D0cIS4KgJRqzNUAxhGq82g==; csm-hit=s-MVPJN5V892YKJQ8EZZC0|1497022218348; ubid-main=131-1294803-4123240; session-id-time=2082787201l; session-id=136-6685036-7158223",
            "Host"            : "www.amazon.com",
            "Pragma"          : "no-cache",
            "User-Agent" :"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
    ]

    public static void main(String[] args) {
        retrieveProducts()
        es.shutdown()
        es.awaitTermination(10l, TimeUnit.MINUTES)
        println '\nTotals: ' + productCounter.toString()
    }

    private static String getProductId(){
        return INITIAL_URL.substring(INITIAL_URL.lastIndexOf('/') + 1)
    }

    private static String getProductUrl(){
        return INITIAL_URL.substring(0, INITIAL_URL.lastIndexOf('/') + 1)
    }

    private static void retrieveProducts(){
        def productVariations = getBody(getProductUrl() + getProductId()).select('#variation_special_size_type, #variation_color_name').select('.swatchAvailable,.swatchSelect').collect {
            it.attr('data-defaultasin')
        }
        if (productVariations.isEmpty()){
            productVariations.add(getProductId())
        }
        productVariations.each { specialSize ->
            getBody(getProductUrl() + specialSize + '?' + SIZE_PARAMETERS.join('&'))
                    .select('select[name=dropdown_selected_size_name]')
                    .select('.dropdownAvailable, .dropdownSelect')
                    .collect { it.val().substring(it.val().indexOf(",") + 1) }.each { size ->
                getPrice(getProductUrl() + size + '?' + (SIZE_PARAMETERS + PRICE_PARAMETERS).join('&'))
            }
        }
    }

    private static void getPrice(String url) {
        es.submit({
            productCounter.addAndGet(1)
            def body = Jsoup.connect(url.trim())
                    .headers(headers)
                    .get().body()
            def price = body.select('#priceblock_ourprice, #priceblock_dealprice, #priceblock_ourprice_lbl')?.find {it.text()?.contains('$')}?.text()
            def size = body.select('select[name=dropdown_selected_size_name]')?.select('option[selected]')?.first()?.text()
            println price + ': size(' + size + '): ' + url
        });
    }

    private static Element getBody(String url) throws Exception {
        def body = Jsoup.connect(url.trim()).headers(headers).get().body()
        if (body.toString().contains('To discuss automated access to Amazon data please contact api-services-support@amazon.com')){
            throw new Exception("banned")
        }
        return body
    }
}
