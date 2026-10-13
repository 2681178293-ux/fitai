package net.wanho.fitai.modules.mall.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import net.wanho.fitai.infra.result.AjaxResult;
import net.wanho.fitai.modules.mall.model.dto.*;
import net.wanho.fitai.modules.mall.model.vo.AdminProductVO;
import net.wanho.fitai.modules.mall.model.vo.AdminSkuVO;
import net.wanho.fitai.modules.mall.model.vo.PrdAttrVO;
import net.wanho.fitai.modules.mall.model.vo.PrdBrandVO;
import net.wanho.fitai.modules.mall.service.AdminProductServiceI;
import net.wanho.fitai.modules.mall.service.PrdBrandServiceI;
import net.wanho.fitai.modules.mall.service.PrdSpuServiceI;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductServiceI adminProductService;
    private final PrdBrandServiceI prdBrandService;
    private final PrdSpuServiceI prdSpuService;


    //查询SPU销售属性（SKU规格维度）
    @GetMapping("/{spuId}/sale-attr")
    public AjaxResult<List<PrdAttrVO>> getSaleAttr(@PathVariable("spuId") Long spuId){
        return AjaxResult.success(adminProductService.getSaleAttr(spuId));
    }

    //分页查询
    @GetMapping("/page")
    public AjaxResult<IPage<AdminProductVO>> findByPage(ProductQueryDTO queryDto){
        return AjaxResult.success(adminProductService.findByPage(queryDto)); // Assuming adminProductService.page() returns an IPage<AdminProductVO>
    }

    //查询商品详情
    @GetMapping("/{spuId}")
    public AjaxResult<AdminProductVO> getDetailById(@PathVariable("spuId") Long spuId) {
        return AjaxResult.success(adminProductService.getDetailById(spuId));
    }

    //品牌下拉列表
    @GetMapping("/brand/select")
    public AjaxResult<List<PrdBrandVO>> getBrandSelect(){
        return AjaxResult.success(prdBrandService.getBrandSelect());
    }

    //查询SPU下的SKU列表
    @GetMapping("/{spuId}/sku")
    public AjaxResult<List<AdminSkuVO>> getSkuList(@PathVariable("spuId") Long spuId) {
        return AjaxResult.success( adminProductService.getSkuList(spuId));
    }

    //新增SKU
    @PostMapping("/{spuId}/sku")
    public AjaxResult<Void> addSku(@PathVariable("spuId") Long spuId, @RequestBody SkuAddDTO addDto){
        adminProductService.addSku(spuId,addDto);
        return AjaxResult.success();
    }

    //新增商品
    @PostMapping
    public AjaxResult<Void> addProduct(@RequestBody ProductAddDTO addDTO){
        adminProductService.addProduct(addDTO);
        return AjaxResult.success();
    }

    //修改商品状态（上架/下架）
    @PutMapping("/{spuId}/status")
    public AjaxResult<Void> updateProductStatus( @PathVariable("spuId") Long spuId,@RequestParam("status") String status){
        prdSpuService.updateProductStatus(spuId,status);
        return AjaxResult.success();
    }

    //修改商品
    @PutMapping("/{spuId}")
    public AjaxResult<Void> updateProduct(@PathVariable("spuId") Long spuId, @RequestBody ProductUpdateDTO updateDto){
        adminProductService.updateProduct(spuId,updateDto);
        return AjaxResult.success();
    }

    //修改SKU
    @PutMapping("/sku/{skuId}")
    public AjaxResult<Void> updateSku(@PathVariable("skuId") Long skuId, @RequestBody SkuUpdateDTO updateDTO) {
        adminProductService.updateSku(skuId, updateDTO);
        return AjaxResult.success();
    }

    //删除SKU
    @DeleteMapping("/sku/{skuId}")
    public AjaxResult<Void> deleteSku(@PathVariable("skuId") Long skuId) {
        adminProductService.deleteSku(skuId);
        return AjaxResult.success();
    }

}
