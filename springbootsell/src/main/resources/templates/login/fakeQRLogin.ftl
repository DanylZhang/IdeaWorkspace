<!DOCTYPE html>
<html>
<head>
    <title>微信登录</title>
    <meta charset="utf-8">
    <link rel="stylesheet" href="https://res.wx.qq.com/connect/zh_CN/htmledition/style/impowerApp3696b4.css">
    <link href="https://res.wx.qq.com/connect/zh_CN/htmledition/images/favicon3696b4.ico" rel="Shortcut Icon">
    <script src="https://res.wx.qq.com/connect/zh_CN/htmledition/js/jquery.min3696b4.js"></script>
</head>

<body style="background-color: rgb(51, 51, 51); padding: 50px;">
<div class="main impowerBox">
    <div class="loginPanel normalPanel">
        <div class="title">微信登录</div>
        <div class="waiting panelContent">
            <div class="wrp_code"><img class="qrcode lightBorder" src="/sell/seller/fakeQRLogin/getQRCode/${qrUUID}">
            </div>
            <div class="info">
                <div class="status status_browser js_status normal" id="wx_default_tip">
                    <p>请使用微信扫描二维码登录</p>
                    <p>“齐鲁阳光文具后台管理系统”</p>
                </div>
                <div class="status status_succ js_status normal" style="display:none" id="wx_after_scan">
                    <i class="status_icon icon38_msg succ"></i>
                    <div class="status_txt">
                        <h4>扫描成功</h4>
                        <p>请在微信中点击确认即可登录</p>
                    </div>
                </div>
                <div class="status status_fail js_status normal" style="display:none" id="wx_after_cancel">
                    <i class="status_icon icon38_msg warn"></i>
                    <div class="status_txt">
                        <h4>您已取消此次登录</h4>
                        <p>您可再次扫描登录，或关闭窗口</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

</body>

<script>
    var websocket = null;
    if ('WebSocket' in window) {
        websocket = new WebSocket('ws://danyl.natappvip.cc/webSocket/${qrUUID}');
    } else {
        alert('该浏览器不支持websocket!');
    }

    websocket.onopen = function (event) {
        console.log('建立连接');
    };

    websocket.onclose = function (event) {
        console.log('连接关闭');
    };

    websocket.onmessage = function (event) {
        console.log('收到消息：' + event.data);
        response = JSON.parse(event.data);
        switch (response.code) {
            case '200':
                window.location.href = "/sell/seller/login?openid=" + response.openid + "&returnUrl=" +"${returnUrl}";
                break;
            case '302':
                $(".js_status").hide();
                $("#wx_after_scan").show();
                break;
            case '400':
                $(".js_status").hide();
                $("#wx_after_cancel").show();
                break;
        }
    };

    websocket.onerror = function (event) {
        alert('websocket通信发生错误！');
    };

    window.onbeforeunload = function (event) {
        websocket.close();
    };
</script>

</body>
</html>
