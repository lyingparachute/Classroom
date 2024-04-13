package com.example.classroom.user.profile;

import com.example.classroom.auth.model.UpdateRequest;
import com.example.classroom.auth.service.UserManagementService;
import com.example.classroom.breadcrumb.BreadcrumbService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.security.Principal;

@Controller
@RequestMapping("/dashboard/profile")
@RequiredArgsConstructor
class UserProfileController {

    private final UserManagementService userManagementService;

    private static final String USER_EDIT_TEMPLATE = "user/user-edit";
    private static final String FIELDS_OF_STUDY_UPLOAD_DIR = "fields-of-study/";

    @GetMapping
    String getUserProfilePage(final Model model,
                              final HttpServletRequest request,
                              final Principal principal) {
        addAttributesBreadcrumbAndUser(principal, model, request);
        model.addAttribute("imagesPath", Path.of("/img").resolve(FIELDS_OF_STUDY_UPLOAD_DIR));
        return "user/user-view";
    }


    @GetMapping("/edit")
    String getEditUserProfilePage(final Model model,
                                  final HttpServletRequest request,
                                  final Principal principal) {
        addAttributesBreadcrumbAndUser(principal, model, request);
        return USER_EDIT_TEMPLATE;
    }

    @PostMapping("/update")
    String updateUserDetails(@Valid @ModelAttribute final UpdateRequest userRequest,
                             final BindingResult result,
                             final HttpServletRequest request,
                             final Principal principal,
                             final RedirectAttributes redirectAttributes,
                             final Model model) {
        if (result.hasErrors()) {
            addAttributesBreadcrumbAndUser(principal, model, request);
            return USER_EDIT_TEMPLATE;
        }
        final var updated = userManagementService.update(userRequest);
        redirectAttributes.addFlashAttribute("editSuccess", updated);
        return "redirect:/dashboard/profile";
    }

    @GetMapping("delete/{id}")
    String deleteAccount(@PathVariable final Long id,
                         final HttpServletRequest request,
                         final RedirectAttributes redirectAttributes) {
        userManagementService.invalidateSession(request);
        userManagementService.removeById(id);
        redirectAttributes.addFlashAttribute("deleteSuccess", "removed");
        return "redirect:/sign-up";
    }

    private void addAttributesBreadcrumbAndUser(final Principal principal,
                                                final Model model,
                                                final HttpServletRequest request) {
        model.addAttribute("crumbs", BreadcrumbService.getBreadcrumbs(request.getRequestURI()));
        model.addAttribute("user", userManagementService.loadUserByUsername(principal.getName()));
    }
}
