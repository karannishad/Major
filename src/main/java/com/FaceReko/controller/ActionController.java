package com.FaceReko.controller;

import com.FaceReko.awscollection.CompareFaces;
import com.FaceReko.model.Assignment;
import com.FaceReko.model.Attendance;
import com.FaceReko.model.AttendanceRecord;
import com.FaceReko.model.User;
import com.FaceReko.repository.AssignmentRepo;
import com.FaceReko.repository.AttendanceRecordRepo;
import com.FaceReko.repository.AttendanceRepo;
import com.FaceReko.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class ActionController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AttendanceRecordRepo attendanceRecordRepo;

    @Autowired
    AttendanceRepo attendanceRepo;

    @Autowired
    AssignmentRepo assignmentRepo;

    @RequestMapping("/uploadAttendance")
    public ModelAndView uploadAttendance(@ModelAttribute AttendanceRecord attendanceRecord, HttpSession httpSession){
        ModelAndView modelAndView=null;
        attendanceRecord.setTeacherid((String) httpSession.getAttribute("id"));
        attendanceRecordRepo.save(attendanceRecord);
        httpSession.setAttribute("attendId",attendanceRecord.getId());
            modelAndView =new ModelAndView("uploadClassImage");
            return modelAndView;

    }



    @RequestMapping("/uploadAttendanceImage")
    public ModelAndView uploadAttendanceImage(@RequestParam("image") MultipartFile file, HttpSession httpSession){
        ModelAndView mav =null;
        try {


            AttendanceRecord attendanceRecord = attendanceRecordRepo.findById((Long)httpSession.getAttribute("attendId")).get();
            attendanceRecord.setImage(file.getBytes());
            attendanceRecordRepo.save(attendanceRecord);
            List<String> result = CompareFaces.getAttendance(attendanceRecord);
            List<Attendance> attendanceList=new ArrayList<>();
            for(String id:result)
            {
                attendanceList.add(new Attendance(id,attendanceRecord));
            }
            Set<Attendance> attendanceSet =new HashSet<>(attendanceList);

            attendanceRepo.saveAll(attendanceSet);
            mav=new ModelAndView("success");
            mav.addObject("studentList",result);
            return mav;

        } catch (IOException e) {
            e.printStackTrace();
        }
        mav=new ModelAndView("fail");
        return mav;

    }

    @RequestMapping("/getAttendance")
    public String getAttendance(Model model,HttpSession httpSession){

        List<Attendance> attendanceList=attendanceRepo.findByStudentId((String)httpSession.getAttribute("id"));
        Collections.reverse(attendanceList);
        model.addAttribute("attendanceList",attendanceList);

        return "showAttendance";
    }

    @RequestMapping("/uploadAssignment")
    public ModelAndView uploadAssignment(@ModelAttribute Assignment assignment,HttpSession httpSession){

        if(assignment!=null){

            assignmentRepo.save(assignment);
            httpSession.setAttribute("assignmentId",assignment.getId());
            return new ModelAndView("uploadAssignmenDoc");
        }

        return new ModelAndView("uploadAssignment");


    }


    @RequestMapping("/uploadAssignmentDoc")
    public ModelAndView uploadAssignmentDoc(@RequestParam("fileField") MultipartFile fileField, HttpSession httpSession){
        ModelAndView mav =null;
        if(!fileField.isEmpty())
        {try {

            Assignment assignment=assignmentRepo.findById((Long)httpSession.getAttribute("assignmentId")).get();

            assignment.setFileField(fileField.getBytes());
            assignmentRepo.save(assignment);

            return new ModelAndView("response").addObject("response","Assignment Uploaded Successfully.");
        }
        catch (Exception e){

        }}

            return new ModelAndView("uploadAssignmenDoc");


    }

    @RequestMapping("/showAssignment")
    public String getAssignment(Model model,HttpSession httpSession){
        User user=userRepository.findByEnrollId((String)httpSession.getAttribute("id"));
        List<Assignment> assignmentList=assignmentRepo.findByBatch(user.getBatch());
        model.addAttribute("assignmentList",assignmentList);

        return "showAssignment";
    }






}