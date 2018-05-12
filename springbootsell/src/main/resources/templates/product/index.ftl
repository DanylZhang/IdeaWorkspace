<html>

<#include "../common/header.ftl">

<body>

<div id="wrapper" class="toggled">
<#--边栏nav-->
    <#include "../common/nav.ftl">

<#--content-->
    <div id="page-content-wrapper">
        <div class="container-fluid">
            <div class="row clearfix">
                <div class="col-md-12 column">
                    <form role="form" action="/sell/seller/product/save" method="post">
                        <div class="form-group">
                            <label for="productName">名称</label>
                            <input name="productName"
                                   type="text"
                                   class="form-control"
                                   id="productName"
                                   value="${(productInfo.productName)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="productPrice">单价</label>
                            <input name="productPrice"
                                   type="number"
                                   step="0.01"
                                   class="form-control"
                                   id="productPrice"
                                   value="${(productInfo.productPrice)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="productStock">库存</label>
                            <input name="productStock"
                                   type="number"
                                   class="form-control"
                                   id="productStock"
                                   value="${(productInfo.productStock)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="productDescription">描述</label>
                            <input name="productDescription"
                                   type="text"
                                   class="form-control"
                                   id="productDescription"
                                   value="${(productInfo.productDescription)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="productIcon">图片</label>
                            <img width="120" height="120" src="${(productInfo.productIcon)!''}" alt="暂无图片">
                            <input name="productIcon"
                                   type="text"
                                   class="form-control"
                                   id="productIcon"
                                   value="${(productInfo.productIcon)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="categoryType">类目</label>
                            <select name="categoryType" class="form-control" id="categoryType">
                                <#list categoryList as category>
                                    <option value="${category.categoryType}"
                                            <#if (productInfo.categoryType)?? && productInfo.categoryType == category.categoryType>selected</#if>>${category.categoryName}</option>
                                </#list>
                            </select>
                        </div>

                        <input type="hidden" name="productId" value="${(productInfo.productId)!''}">
                        <button type="submit" class="btn btn-default">提交</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>