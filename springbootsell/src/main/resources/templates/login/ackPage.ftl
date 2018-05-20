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
    <div class="weui-msg__icon-area"><i class="weui-icon-success weui-icon_msg"></i></div>
    <div class="weui-msg__text-area">
        <h2 class="weui-msg__title">扫码成功</h2>
        <p class="weui-msg__desc">即将登录齐鲁阳光文具后台管理系统，请确认是本人操作</p>
    </div>
    <div class="weui-msg__opr-area">
        <p class="weui-btn-area">
            <a href="http://danyl.natappvip.cc/sell/seller/fakeQRLogin/ack/${qrUUID}?openid=${openid}"
               class="weui-btn weui-btn_primary">确认登录</a>
            <a href="ttp://danyl.natappvip.cc/sell/seller/fakeQRLogin/cancel/${qrUUID}?openid=${openid}"
               class="weui-btn weui-btn_default">取消登录</a>
        </p>
    </div>
    <div class="weui-msg__extra-area">
        <div class="weui-footer">
            <p class="weui-footer__links">
                <a href="javascript:void(0);" class="weui-footer__link">联系我们</a>
            </p>
            <p class="weui-footer__text">Copyright © 2008-2016 danyl.natappvip.cc</p>
        </div>
    </div>
</div>

</body>
</html>
