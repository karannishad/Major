package com.FaceReko.controller;

import com.FaceReko.model.AttendanceRecord;
import com.FaceReko.model.Student;
import com.FaceReko.repository.StudentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    StudentRepository studentRepository;



    @RequestMapping("/checkLogin")
    public String loginRequest(HttpSession httpSession){
        Student student=studentRepository.findByEnrollId("0818IT151019");


            httpSession.setAttribute("id",student.getEnrollId());
        return "home";

    }
    @RequestMapping("/log")
    public String login(){

        return "login.html";

    }
    @RequestMapping("/studentregistration")
    public ModelAndView registerUser(@ModelAttribute Student student, HttpSession httpSession) throws Exception {
        ModelAndView mav=null;
        if(httpSession.getAttribute("id")==null)
            httpSession.setAttribute("id",student.getEnrollId());
        studentRepository.save(student);
        mav=new ModelAndView("setImage", HttpStatus.CREATED);
        return mav;
    }

    @RequestMapping("/logout")
    public String logoutRequest(HttpSession httpSession){

        httpSession.invalidate();;
        return "redirect:/";
    }
}