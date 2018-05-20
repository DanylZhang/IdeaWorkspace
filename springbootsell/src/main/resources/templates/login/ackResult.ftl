<!DOCTYPE html>
<html>
<head>
    <title>微信登录</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no">

    <link rel="stylesheet" href="http://jqweui.com/dist/lib/weui.min.css">
    <link rel="stylesheet" href="http://jqweui.com/dist/css/jquery-weui.css">

    <script src="http://jqweui.com/dist/lib/jquery-2.1.4.js"></script>
    <script src="http://jqweui.com/dist/js/jquery-weui.js"></script>
    <script src="http://jqweui.com/dist/js/city-picker.js"></script>
</head>

<body ontouchstart>

<div class="weui-msg">
    <#if msg=='200'>
        <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title">成功登录</h2>
            <p class="weui-msg__desc">您已成功登录齐鲁阳光文具后台管理系统</p>
            <p class="weui-msg__desc"><a href="javascript:closePage()">关闭此页面</a></p>
        </div>
    <#elseif msg=='400'>
        <div class="weui-msg__icon-area"><i class="weui-icon-info weui-icon_msg"></i></div>
        <div class="weui-msg__text-area">
            <h2 class="weui-msg__title">登录失败</h2>
            <p class="weui-msg__desc">您已取消登录齐鲁阳光文具后台管理系统</p>
            <p class="weui-msg__desc"><a href="javascript:closePage()">关闭此页面</a></p>
        </div>
    </#if>
</div>

<script>
    function closePage() {
        WeixinJSBridge.invoke('closeWindow', {}, function (res) {
            // console.log(res.err_msg);
        });
    }
</script>

</body>
</html>
