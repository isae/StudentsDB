package ru.ifmo.ctddev.isaev.solanteq.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.ifmo.ctddev.isaev.solanteq.dao.MainDAO;
import ru.ifmo.ctddev.isaev.solanteq.helpers.Pair;
import ru.ifmo.ctddev.isaev.solanteq.pojo.Employee;
import ru.ifmo.ctddev.isaev.solanteq.pojo.Position;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    MainDAO dao;

    private static Map<String, String> getResponseObject(boolean ok, String goodMsg, String badMsg) {
        Map<String, String> result = new HashMap<>();
        result.put("status", ok ? "success" : "danger");
        result.put("message", ok ? goodMsg : badMsg);
        return result;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getPage(HttpServletRequest request,
                                HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("main.jsp");
        Pair<List<Employee>, Collection<Position>> pair = dao.getAllEmployeesAndPositions();
        mav.addObject("employees", pair.getFirst());
        mav.addObject("positions", pair.getSecond());
        return mav;
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> getAll(HttpServletRequest request,
                               HttpServletResponse response) {
        Map<String, Object> result = new HashMap<>();
        Pair<List<Employee>, Collection<Position>> pair = dao.getAllEmployeesAndPositions();
        result.put("employees", pair.getFirst());
        result.put("positions", pair.getSecond());
        return result;
    }

    @RequestMapping(value = "/editEmployee", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    Map<String, String> editEmployee(@RequestBody Employee employee,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        boolean ok = dao.updateEmployee(employee);
        return getResponseObject(ok,
                String.format("Сотрудник с ID %s успешно изменён", employee.getEmployeeID()),
                String.format("Не удалось изменить сотрудника с ID %s", employee.getEmployeeID()));
    }

    @RequestMapping(value = "/addEmployee", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    Map<String, String> addEmployee(@RequestBody Employee employee,
                                    HttpServletRequest request,
                                    HttpServletResponse response) {
        boolean ok = dao.addEmployee(employee);
        return getResponseObject(ok,
                String.format("Добавлен новый сотрудник: %s %s %s",
                        employee.getSurname(),
                        employee.getFirstName(),
                        employee.getPatronymic()),
                "Не удалось добавить сотрудника");
    }

    @RequestMapping(value = "/deleteEmployee", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, String> deleteEmployee(@RequestParam("employeeID") int employeeID,
                                       HttpServletRequest request,
                                       HttpServletResponse response) {
        boolean ok = dao.deleteEmployee(employeeID);
        return getResponseObject(ok,
                String.format("Сотрудник с ID %s успешно удалён", employeeID),
                String.format("Не удалось удалить сотрудника с ID %s", employeeID));
    }
}