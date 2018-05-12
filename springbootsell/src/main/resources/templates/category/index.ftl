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
                    <form role="form" action="/sell/seller/category/save" method="post">
                        <div class="form-group">
                            <label for="categoryName">名称</label>
                            <input name="categoryName"
                                   type="text"
                                   class="form-control"
                                   id="categoryName"
                                   value="${(productCategory.categoryName)!''}"/>
                        </div>
                        <div class="form-group">
                            <label for="categoryType">type</label>
                            <input name="categoryType"
                                   type="number"
                                   step="1"
                                   class="form-control"
                                   id="categoryType"
                                   value="${(productCategory.categoryType)!''}"/>
                        </div>

                        <input type="hidden" name="categoryId" value="${(productCategory.categoryId)!''}">
                        <button type="submit" class="btn btn-default">提交</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>