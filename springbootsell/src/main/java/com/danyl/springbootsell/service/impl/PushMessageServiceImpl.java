package com.danyl.springbootsell.service.impl;

import com.danyl.springbootsell.dto.OrderDTO;
import com.danyl.springbootsell.service.PushMessageService;
import com.lly835.bestpay.utils.DateUtil;
import com.lly835.bestpay.utils.RandomUtil;
import javafx.util.converter.DateTimeStringConverter;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class PushMessageServiceImpl implements PushMessageService {

    @Autowired
    private WxMpService wxMpService;

    @Override
    public void orderStatus(OrderDTO orderDTO) {
        String openid = "o4oKG1BqdNzOclX3rLYge7ylifHw";
        String templateId = "AISPsHA939vWnLgGdhhM18sTGtQWT4OC4nrcLGoFtmQ";

        WxMpTemplateMessage wxMpTemplateMessage = new WxMpTemplateMessage();
        wxMpTemplateMessage.setToUser(openid);
        wxMpTemplateMessage.setTemplateId(templateId);

        List<WxMpTemplateData> data = Arrays.asList(
                new WxMpTemplateData("first", "您收到一条齐鲁阳光文具商家后台登陆验证码，5分钟内有效，请勿泄露！"),
                new WxMpTemplateData("keyword1", "" + RandomUtils.nextInt(100000, 999999)),
                new WxMpTemplateData("keyword2", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss"))
        );
        wxMpTemplateMessage.setData(data);

        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(wxMpTemplateMessage);
        } catch (WxErrorException e) {
            log.error("【微信模板消息】发送失败，{}", e);
        }
    }
}
