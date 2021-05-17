package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Perms;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description 权限树实体
 * @date 2019/5/6 0006 18:06
 */
@Setter
@Getter
public class PermsVo extends Perms {
	private List<PermsVo> children;

}
