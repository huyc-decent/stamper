package com.yunxi.stamper.entityVo;

import com.yunxi.stamper.entity.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/5/10 0010 15:27
 */
@Setter
@Getter
public class RoleVo extends Role {
	private List<Integer> permIds;//权限id列表
	private boolean checked;//true:拥有权限  false:没有权限
	private boolean writer = true;//true：可操作  false：不可操作

	/**
	 * 以下参数仅供'角色管理-列表展示功能'使用
	 */
	private String createName;
	private String updateName;

}
