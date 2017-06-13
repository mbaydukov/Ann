import com.mb.ann.entity.Product
import com.mb.ann.entity.ProductGroup
import com.mb.ann.utils.Parser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class Amazon implements Parser {

    private def SIZE_PARAMETERS = ["th=1", "pcs=1"]
    private def PRICE_PARAMETERS = ["psc=1"]
    private ExecutorService es = Executors.newFixedThreadPool(10)
    private Map<String, String> headers = [
            "Accept"          : "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Encoding" : "gzip, deflate, sdch, br",
            "Accept-Language" : "en-US,en;q=0.8",
            "Cache-Control"   : "no-cache",
            "Connection"      : "keep-alive",
            "Cookie"          : "csm-sid=193-6609007-8904256; x-amz-captcha-1=1497299517625947; x-amz-captcha-2=ToC8tjCRFe8YJj7xHoWxow==; x-wl-uid=1rkMJMXAW0Y6pEMYgRR4m2ECRtlgnnc8BGsiCXqB+GDvWONEdMB9vR4rOyzBeVK6iCaGU3VHWwCY=; csm-hit=s-3JEDFQ40Y8H6ANH30JG5|1497292318372; session-token=2rGE8hwIKxTK0KS7E8G2KqauxQY0ivbwcUrYYqh9vl6x3b9aioe6ctVLrN37iZBaPLO06jxxAVMRiMyEMx0AK9tbgZ6lfaED2HdgKZa+PmSM4z0pp7mcmIUebmIWk7zuwaMtazMg2A3bM8RY2jwnMGw3ANdBGt8+0Xe7CPgaMEoZMRi2wS8dgAPLub8YU1xCtTM4wstyQCmDoXirRKraxRyAp5zM8HVuhgEyzWTQknHf+w03bU+Mix4zNfPopFbZZhnp8OxJVC0=; ubid-main=132-9512458-0875700; session-id-time=2082787201l; session-id=138-3662034-1120667",
            "Host"            : "www.amazon.com",
            "Pragma"          : "no-cache",
            "User-Agent" :"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36"
    ]
    private List<Product> products = []

    public List<Product> parse(ProductGroup productGroup) throws Exception {
        retrieveProducts(productGroup)
        es.shutdown()
        es.awaitTermination(10l, TimeUnit.MINUTES)
        return products
    }

    private String getProductId(ProductGroup productGroup){
        def url = productGroup.getUrl()
        return url.substring(url.lastIndexOf('/') + 1)
    }

    private String getProductUrl(ProductGroup productGroup){
        def url = productGroup.getUrl()
        return url.substring(0, url.lastIndexOf('/') + 1)
    }

    private void retrieveProducts(ProductGroup productGroup) throws Exception {
        def productUrl = getProductUrl(productGroup)
        def productId = getProductId(productGroup)
        def productVariations = getBody(productUrl + productId).select('#variation_special_size_type, #variation_color_name').select('.swatchAvailable,.swatchSelect').collect {
            it.attr('data-defaultasin')
        }
        if (productVariations.isEmpty()){
            productVariations.add(productId)
        }
        productVariations.each { specialSize ->
            getBody(productUrl + specialSize + '?' + SIZE_PARAMETERS.join('&'))
                    .select('select[name=dropdown_selected_size_name]')
                    .select('.dropdownAvailable, .dropdownSelect')
                    .collect { it.val().substring(it.val().indexOf(",") + 1) }
                    .each { size ->
                def url = productUrl + size + '?' + (SIZE_PARAMETERS + PRICE_PARAMETERS).join('&')
                getPrice(productGroup, url)
            }
        }
    }

    private void getPrice(ProductGroup productGroup, String url) throws Exception {
        es.submit({
            def body = Jsoup.connect(url.trim())
                    .headers(headers)
                    .get().body()
            def price = body.select('#priceblock_ourprice, #priceblock_dealprice, #priceblock_ourprice_lbl')?.find {it.text()?.contains('$')}?.text()
            def size = body.select('select[name=dropdown_selected_size_name]')?.select('option[selected]')?.first()?.text()
            products.add(new Product(productGroup.getId(), url, price != null ? Double.valueOf(price.replace('$', '')) : null, size))
        });
    }

    private Element getBody(String url) throws Exception {
        def body = Jsoup.connect(url.trim()).headers(headers).get().body()
        if (body.toString().contains('To discuss automated access to Amazon data please contact api-services-support@amazon.com')){
            throw new Exception("banned")
        }
        return body
    }

}
