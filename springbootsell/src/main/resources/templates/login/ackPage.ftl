<!DOCTYPE html>
<html>
<head>
    <title>微信登录</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="https://res.wx.qq.com/connect/zh_CN/htmledition/style/impowerApp3696b4.css">
    <link href="https://res.wx.qq.com/connect/zh_CN/htmledition/images/favicon3696b4.ico" rel="Shortcut Icon">
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/jquery.min3696b4.js"></script>
</head>
<body>
<div class="main impowerBox">
    <div class="loginPanel normalPanel">
        <div class="title">微信登录</div>
        <div class="waiting panelContent">
            <div class="info">
                <div class="status status_browser js_status" id="wx_default_tip">
                    <p>确认登录</p>
                    <p>“齐鲁阳光文具商家后台管理系统”</p>
                </div>
            </div>
        </div>
        <div class="waiting panelContent">
            <div class="info">
                <div class="status status_browser js_status" id="wx_cancel_tip">
                    <p>取消登录</p>
                    <p>“齐鲁阳光文具商家后台管理系统”</p>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $('#wx_default_tip').click(function (event) {
        window.location.href = "http://danyl.natappvip.cc/sell/seller/fakeQRLogin/ack";
    });
    $('#wx_cancel_tip').click(function (event) {
        window.location.href = "http://danyl.natappvip.cc/sell/seller/fakeQRLogin/cancel";
    });
</script>
</body>
</html>
