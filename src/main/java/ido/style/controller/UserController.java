package ido.style.controller;

import ido.style.dto.*;
import ido.style.service.*;
import jakarta.servlet.http.HttpSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger log = LogManager.getLogger(UserController.class);
    @Autowired private UserService userService;


    @Autowired
    private ProductService productService;
    @Autowired
    private StyleProductService styleProductService;


    @GetMapping("/login")
    public String user_login(
            Authentication authentication,
            Model model
    ) {
        // 이미 로그인이 되어 있는 상태이다
        if(Objects.nonNull(authentication)){
            return "redirect:/";
        }

        List<StyleCategoryDTO> styleCategories = styleProductService.get_categories();
        List<CategoryDTO> categories = productService.get_categories();
        model.addAttribute("styleCategories", styleCategories);
        model.addAttribute("categories", categories);
        return "user/login";
    }
    /************************************************/
    @GetMapping("/join")
    public String user_join(
            @ModelAttribute UserDTO userDTO,
            Authentication authentication,
            Model model
    ) {
        // 이미 로그인이 되어 있는 상태이다
        if(Objects.nonNull(authentication)){
            return "redirect:/";
        }
        List<StyleCategoryDTO> styleCategories = styleProductService.get_categories();
        List<CategoryDTO> categories = productService.get_categories();
        model.addAttribute("styleCategories", styleCategories);
        model.addAttribute("categories", categories);
        return "user/join";
    }

    @PostMapping("/join")
    public String user_join(
            @ModelAttribute UserDTO userDTO,
            BindingResult bindingResult,
            HttpSession session
    ) {
        if(bindingResult.hasErrors()){
            log.error("에러 발생!");
            log.error(bindingResult.getAllErrors());
            return "user/join";
        }

        boolean joinResult = userService.join_user(userDTO);
        // 가입 성공이면 login 화면으로, 실패라면 회원가입 화면으로.
        return joinResult ? "redirect:/user/login" : "user/join";
    }

    /************************ 유저 정보 ***************************/
    @GetMapping("/myPage")
    public String user_myPage(
            @RequestParam(defaultValue = "1") Integer categoryNo,
            String sort,

            Authentication authentication,
            @AuthenticationPrincipal UserDTO user,

            Model model
    ){
        if(!(Objects.nonNull(authentication))){
            return "redirect:/user/login";
        }

        model.addAttribute("categoryNo", categoryNo); // 정렬 a태그에 사용

        List<StyleStoreCategoryDTO> styleStoreCategories = productService.get_style_store_categories(); // 편의 카테고리
        model.addAttribute("styleStoreCategories", styleStoreCategories);

        List<LovesDTO> loves = productService.get_loves_by_user(categoryNo, user, sort); // 찜 목록
        model.addAttribute("loves", loves);

        // 상위 header에 사용되는 카테고리
        List<StyleCategoryDTO> styleCategories = styleProductService.get_categories();
        List<CategoryDTO> categories = productService.get_categories();
        model.addAttribute("styleCategories", styleCategories);
        model.addAttribute("categories", categories);

        return "user/myPage";
    }

    /******************************************************************/
    @GetMapping("/style-upload")
    public String style_upload(
            @ModelAttribute UserDTO userDTO,
            Authentication authentication,
            Model model
         ){

        if(!(Objects.nonNull(authentication))){
            return "redirect:/user/login";
        }

        List<StyleCategoryDTO> styleCategories = styleProductService.get_categories();
        List<CategoryDTO> categories = productService.get_categories();
        model.addAttribute("styleCategories", styleCategories);
        model.addAttribute("categories", categories);
        return "user/style-upload";
    }

    @PostMapping("/style-upload")
    public String style_upload(
            StyleProductDTO styleProductDTO
    )
    {
        userService.add_style_product(styleProductDTO);
        return "redirect:/styleCategory";
    }


}
