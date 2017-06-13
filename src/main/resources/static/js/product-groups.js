
var addProductGroupDialog = function(){
    bootbox.dialog({
            title: "Add new Product Group",
            message: $(tmpl("add-product-group-dialog-template", {}).trim()),
            buttons: {
                success: {
                    label: "Add",
                    className: "btn-primary",
                    callback: function () {
                        var productGroup = {
                            url: $('#product-group-url').val(),
                            description: $('#product-group-description').val()
                        };
                        if (productGroup.url.trim().length == 0 || productGroup.description.trim().length == 0){
                            bootbox.alert("Please, provide url and description");
                            return false;
                        }
                        $.ajax({
                            url: '/product-group/add',
                            type: 'POST',
                            data: JSON.stringify(productGroup),
                            contentType: 'application/json',
                            success: function(){
                                loadProductGroups();
                                bootbox.hideAll()
                            },
                            error: function(err){
                                bootbox.alert("Product Group cannot be added");
                                return false;
                            }
                        });
                        return false;
                    }
                },
                cancel : {
                    label: "Cancel",
                    className: "btn-default btn-cancel"
                }
            }
        }
    )
};

var loadProductGroups = function(){
    var table = $('.product-queue tbody');
    table.empty();
    $.ajax({
        url: '/product-group/list',
        type: 'GET',
        success: function(productGroups){
            $.each(productGroups, function(idx, productGroup) {
                productGroup['updateTimeFormatted'] = productGroup.updateTime != null ? moment(productGroup.updateTime).format('MMM DD HH:mm') : '-';
                var tr = $(tmpl('product-group', productGroup).trim());
                table.append(tr);
                tr.data('productGroup', productGroup)
            });
        }
    });
};

var parseProductGroup = function(productGroupId){
    $.ajax({
        url: '/product-group/parse',
        type: 'GET',
        data: {productGroupId: productGroupId},
        success: function(){
            loadProductGroups();
        },
        error: function(err){
            bootbox.alert("Product Group cannot be parsed");
            return false;
        }
    });
};

$(document).ready(function () {
    loadProductGroups();
    $(document).on('click', '.parse-product-group', function(e){
        var productGroup = $(e.target).closest('tr').data('productGroup');
        parseProductGroup(productGroup.id);
    });
});