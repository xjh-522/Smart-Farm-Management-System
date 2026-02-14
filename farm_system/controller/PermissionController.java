package com.itbaizhan.farm_system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itbaizhan.farm_common.result.BaseResult;
import com.itbaizhan.farm_system.entity.Permission;
import com.itbaizhan.farm_system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    @Autowired
    private PermissionService permissionService;
    @GetMapping("/list")
    public BaseResult<IPage<Permission>> getPermissionList(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                           @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                                           @RequestParam(value = "permissionName", required = false) String permissionName)
    {
        IPage<Permission> permissionList = permissionService.getPermissionList(pageNum, pageSize, permissionName);
        return BaseResult.ok(permissionList);

    }
    @GetMapping("/getPermissionById")
    public BaseResult<Permission> selectById(@RequestParam("id") Long id){
        Permission permission = permissionService.SelectById(id);
        return BaseResult.ok(permission);
    }
    @GetMapping("/all")
    public BaseResult<List<Permission>> AllPermission(){
        List<Permission> allPermission = permissionService.getAllPermissions();
        return BaseResult.ok(allPermission);
    }
    @PostMapping("/addPermission")
    public BaseResult<String> addPermission(@RequestBody Permission permission){
        permissionService.AddPermission(permission);
        return BaseResult.ok();
    }
    @GetMapping("/selectList")
    public BaseResult<List<Permission>> SelectList(){
        List<Permission> permissionSelectList = permissionService.getPermissionSelectList();
        return BaseResult.ok(permissionSelectList);

    }
    @PutMapping("/updatePermission")
    public BaseResult<String> update(@RequestBody @Validated Permission permission){
        permissionService.UpdatePermission(permission);
        return BaseResult.ok();
    }

    @DeleteMapping("/deletePermission")
    public BaseResult deletePermission(@RequestParam("ids") String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        permissionService.DeletePermission(idList);
        return BaseResult.ok();
    }

}
