package com.chaohu.qa.ttp.web.controller;

import com.chaohu.qa.ttp.api.service.IProductService;
import com.chaohu.qa.ttp.api.vo.req.CreateProductReq;
import com.chaohu.qa.ttp.web.Response.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author wangmin
 * @date 2022/9/21 09:34
 */
@RequestMapping("/ttp/products")
@RestController()
public class ProductController {
    @Resource
    private IProductService iProductService;

    @GetMapping("/group")
    public Result<Object> groupList() {
        return Result.success(iProductService.groupList());
    }

    @GetMapping
    public Result<Object> list(@RequestParam("pageNo") int pageNo,
                               @RequestParam("pageSize") int pageSize) {
        return Result.success(iProductService.list(pageNo, pageSize));
    }

    @PostMapping()
    public Result<Object> create(@RequestBody @Valid CreateProductReq createProductReq) {
        iProductService.create(createProductReq);
        return Result.success("创建成功");
    }

    @PutMapping
    public Result<Object> update(@RequestBody CreateProductReq createProductReq) {
        iProductService.update(createProductReq);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<Object> delete(@PathVariable("id") Integer id) {
        iProductService.delete(id);
        return Result.success("删除成功");
    }
}
