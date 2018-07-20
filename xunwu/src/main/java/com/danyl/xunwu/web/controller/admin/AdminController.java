package com.danyl.xunwu.web.controller.admin;

import com.danyl.xunwu.base.ApiResponse;
import com.danyl.xunwu.entity.SupportAddress;
import com.danyl.xunwu.service.IAddressService;
import com.danyl.xunwu.service.IHouseService;
import com.danyl.xunwu.service.ServiceResult;
import com.danyl.xunwu.web.dto.HouseDTO;
import com.danyl.xunwu.web.dto.SupportAddressDTO;
import com.danyl.xunwu.web.form.HouseForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private IAddressService addressService;

    @Resource
    private IHouseService houseService;

    @GetMapping("/")
    public String HomePage() {
        return "redirect:admin/center";
    }

    @GetMapping("center")
    public String adminCenterPage() {
        return "admin/center";
    }

    @GetMapping("welcome")
    public String WelcomePage() {
        return "admin/welcome";
    }

    @GetMapping("login")
    public String adminLoginPage() {
        return "admin/login";
    }

    @GetMapping("houses")
    public String housePage() {
        return "admin/house-show";
    }

    @GetMapping("house/list")
    public String houseListPage() {
        return "admin/house-list";
    }

    @GetMapping("add/house")
    public String addHousePage() {
        return "admin/house-add";
    }

    @PostMapping(value = "/upload/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        String filename = file.getOriginalFilename();
        File target = new File("D:/" + filename);
        try {
            file.transferTo(target);
        } catch (IOException e) {
            log.error("file transferTo error: {}", e.getMessage());
            return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
        }

        return ApiResponse.ofSuccess(null);
    }

    /**
     * 新增房源接口
     * @param houseForm
     * @param bindingResult
     * @return
     */
    @PostMapping("admin/add/house")
    @ResponseBody
    public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add") HouseForm houseForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
        }

        if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
            return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), "必须上传图片");
        }

        Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());
        if (addressMap.keySet().size() != 2) {
            return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
        }

        ServiceResult<HouseDTO> result = houseService.save(houseForm);
        if (result.isSuccess()) {
            return ApiResponse.ofSuccess(result.getResult());
        }

        return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
    }
}