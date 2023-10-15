package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Category;
import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @GetMapping("/categories")
    public String listAll(Model model){
        // list categories in hierachical form
        model.addAttribute("categories", categoryService.listAll());
        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String showForm(Model model){

        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "New category");

        List<Category> categoryList = categoryService.listCategoryByTreeInForm();
        model.addAttribute("categories", categoryList);

        return "categories/categories-form";
    }

    @GetMapping("/categories/edit/{id}")
    public String showForm(Model model, @PathVariable("id") Integer id,
                           RedirectAttributes redirectAttributes){
        try {
            Category category = categoryService.findById(id);
            model.addAttribute("pageTitle", "New category");
            List<Category> categoryList = categoryService.listCategoryByTreeInForm();

            model.addAttribute("category", category);
            model.addAttribute("categories", categoryList);
            return "categories/categories-form";
        }
        catch (CategoryNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/categories";
        }
    }

    @PostMapping("/categories/save")
    public String save(@ModelAttribute("category")Category category,
                       @RequestParam("imageInput")MultipartFile image,
                       RedirectAttributes redirectAttributes) throws IOException {
        if(category.getId() == null){
            redirectAttributes.addFlashAttribute("message", "Category added successfully");
        }
        else{
            redirectAttributes.addFlashAttribute("message", "Category updated successfully");
        }
        String fileName = StringUtils.cleanPath(image.getOriginalFilename());
        if(!fileName.isEmpty()){
            category.setImage(fileName);
            Category saved = categoryService.save(category);
            String uploadDir = "../categories-images/" + saved.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, image);
        }
        else{
            categoryService.save(category);
        }
        return "redirect:/categories";
    }
}
