package com.yunxi.stamper.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.yunxi.stamper.sys.context.SpringContextUtils;
import com.yunxi.stamper.sys.error.base.PrintException;
import com.yunxi.stamper.entity.*;
import com.yunxi.stamper.entityVo.*;
import com.yunxi.stamper.mapper.FlowMapper;
import com.yunxi.stamper.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhf_10@163.com
 * @Description
 * @date 2019/6/5 0005 11:04
 */
@Service
public class IFlowService implements FlowService {
	@Autowired
	private FlowMapper mapper;
	@Autowired
	private FlowNodeService flowNodeService;
	@Autowired
	private RelateFlowDepartmentService relateFlowDepartmentService;
	@Autowired
	private UserService userService;
	@Autowired
	private DepartmentService departmentService;

	@Override
	@Transactional
	public void update(Flow flow) {
		int updateCount = 0;
		if (flow != null && flow.getId() != null) {
			flow.setUpdateDate(new Date());
			updateCount = mapper.updateByPrimaryKey(flow);
		}
		if (updateCount != 1) {
			new PrintException("审批流程更新失败");
		}
	}

	/**
	 * 查询审批流程信息、流程节点列表信息、所属组织列表信息
	 *
	 * @param flowId 审批流程ID
	 * @return
	 */
	@Override
	public FlowVoAdd getFlowInfo(Integer flowId) {
		if (flowId == null) {
			return null;
		}

		/**
		 * 查询审批流程信息
		 */
		FlowVoAdd flow = mapper.selectByVo(flowId);

		/**
		 * 查询审批流程节点信息
		 */
		List<FlowVoAddEntity> flowNodes = flowNodeService.getVoByFlow(flowId);
		flow.setFlowNodeList(flowNodes);

		/**
		 * 查询组织列表信息
		 */
		List<Integer> departmentIds = relateFlowDepartmentService.getDepartmentByFlow(flowId);
		flow.setDepartmentIds(departmentIds);

		return flow;
	}

	@Override
	@Transactional
	public void del(Flow flow) {
		int delCount = 0;
		if (flow != null && flow.getId() != null) {
			flow.setDeleteDate(new Date());
			delCount = mapper.updateByPrimaryKey(flow);
		}
		if (delCount != 1) {
			throw new PrintException("审批流程删除失败");
		}
	}

	@Override
	public Flow get(Integer flowId) {
		if (flowId != null) {
			Example example = new Example(Flow.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("id", flowId);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	@Transactional
	public void add(Flow flow) {
		int addCount = 0;
		if (flow != null) {
			flow.setCreateDate(new Date());
			addCount = mapper.insert(flow);
		}
		if (addCount != 1) {
			throw new PrintException("添加审批流程失败");
		}
	}

	@Override
	public Flow getByOrgAndName(Integer orgId, String name) {
		if (orgId != null && StringUtils.isNotBlank(name)) {
			Example example = new Example(Flow.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId)
					.andEqualTo("name", name);
			return mapper.selectOneByExample(example);
		}
		return null;
	}

	@Override
	public List<Flow> getByOrg(Integer orgId) {
		if (orgId != null) {
			Example example = new Example(Flow.class);
			example.createCriteria().andIsNull("deleteDate")
					.andEqualTo("orgId", orgId);
			example.orderBy("createDate").desc();
			return mapper.selectByExample(example);
		}
		return null;
	}

	/**
	 * 查询指定组织列表下、指定名称的审批流程数量
	 *
	 * @param departmentIds 组织ID列表
	 * @param name          名称
	 * @return
	 */
	@Override
	public Integer getByDepartmentAndName(List<Integer> departmentIds, @NotEmpty String name) {
		if (departmentIds != null && !departmentIds.isEmpty() && StringUtils.isNotBlank(name)) {
			Integer count = mapper.selectByDepartmentAndName(departmentIds, name);
			return count;
		}
		throw new PrintException("请求参数有误");
	}

	/**
	 * 添加审批流程
	 *
	 * @param flowVo 审批流程实例
	 */
	@Override
	@Transactional
	public void addFlow(@NotNull UserInfo userInfo, @NotNull FlowVoAdd flowVo) {
		if (flowVo == null) {
			throw new PrintException("审批流程添加失败，流程实例不存在");
		}

		/**
		 * 添加审批流程
		 */
		Flow flow = new Flow();
		flow.setName(flowVo.getName());
		flow.setRemark(flowVo.getRemark());
		flow.setStatus(flowVo.getStatus());
		flow.setOrgId(userInfo.getOrgId());
		flow.setUserId(userInfo.getId());
		flow.setUserName(userInfo.getUserName());
		add(flow);

		/**
		 * 添加审批流程节点列表
		 */
		createFlowNode(flowVo.getFlowNodeList(), flow);

		/**
		 * 添加审批流程-组织关联信息
		 */
		List<Integer> departmentIds = flowVo.getDepartmentIds();
		for (int i = 0; i < departmentIds.size(); i++) {
			Integer departmentId = departmentIds.get(i);
			RelateFlowDepartment rdf = relateFlowDepartmentService.get(departmentId, flow.getId());
			if (rdf == null) {
				rdf = new RelateFlowDepartment();
				rdf.setFlowId(flow.getId());
				rdf.setDepartmentId(departmentId);
				relateFlowDepartmentService.add(rdf);
			}
		}
	}

	/**
	 * 添加审批流程节点列表
	 *
	 * @param flowNodeList 流程节点列表
	 * @param flow         审批流程对象
	 */
	private void createFlowNode(List<FlowVoAddEntity> flowNodeList, Flow flow) {
		/**
		 * 添加校验1:动态主管审批,必须按照从下到上的审批流程
		 */
		FlowNode preFlowNode = null;

		for (int i = 0; i < flowNodeList.size(); i++) {
			FlowVoAddEntity entity = flowNodeList.get(i);

			String type = entity.getType();//当前节点审批方式
			if (StringUtils.isBlank(type)) {
				throw new PrintException("步骤" + i + "的审批方式不能为空");
			}
			//依次审批
			if ("list".equalsIgnoreCase(type)) {
				List<FlowVoAddEntityKV> users = entity.getUser();//当前节点审批人列表
				if (users == null || users.size() == 0) {
					throw new PrintException("步骤" + i + "的审批人不能为空");
				}

				if (users.size() > 1) {
					throw new PrintException("步骤" + i + "依次审批时只能选择一个审批人");
				}

				//添加审批节点
				FlowNode node = new FlowNode();
				node.setOrderNo(i);
				node.setFlowId(flow.getId());
				node.setType("list");
				node.setManagerJson(JSONObject.toJSONString(users));
				flowNodeService.add(node);

				preFlowNode = node;
			}
			//会签
			else if ("or".equalsIgnoreCase(type)) {

				List<FlowVoAddEntityKV> users = entity.getUser();//当前节点审批人列表
				if (users == null || users.size() == 0) {
					throw new PrintException("步骤" + i + "的审批人不能为空");
				}

				if (users.size() == 1) {
					throw new PrintException("步骤" + i + "会签必须选择多个审批人");
				}

				//添加审批节点
				FlowNode node = new FlowNode();
				node.setOrderNo(i);
				node.setFlowId(flow.getId());
				node.setType("or");
				node.setManagerJson(JSONObject.toJSONString(users));
				flowNodeService.add(node);

				preFlowNode = node;
			}

			//或签
			else if ("and".equalsIgnoreCase(type)) {

				List<FlowVoAddEntityKV> users = entity.getUser();//当前节点审批人列表
				if (users == null || users.size() == 0) {
					throw new PrintException("步骤" + i + "的审批人不能为空");
				}

				if (users.size() == 1) {
					throw new PrintException("步骤" + i + "或签必须选择多个审批人");
				}

				//添加审批节点
				FlowNode node = new FlowNode();
				node.setOrderNo(i);
				node.setFlowId(flow.getId());
				node.setType("and");
				node.setManagerJson(JSONObject.toJSONString(users));
				flowNodeService.add(node);

				preFlowNode = node;
			}

			//抽象指定(直接主管,第1级主管....)
			else if ("manager".equalsIgnoreCase(type)) {

				List<FlowVoAddEntityKV> users = entity.getUser();//当前节点审批人列表
				if (users == null || users.size() == 0) {
					throw new PrintException("步骤" + i + "不能为空");
				}

				if (users.size() > 1) {
					throw new PrintException("步骤" + i + "不能选择多个审批人");
				}

				//添加审批节点
				FlowNode node = new FlowNode();
				node.setOrderNo(i);
				node.setFlowId(flow.getId());
				node.setType("manager");
				node.setManagerJson(JSONObject.toJSONString(users));
				flowNodeService.add(node);

				preFlowNode = node;
			}

			//审批人自选
			else if ("optional".equalsIgnoreCase(type)) {
				/**
				 * 第1个节点 审批无法选择'自选模式'
				 */
				if (i < 1) {
					throw new PrintException("自选节点不能放在首位");
				}

				/**
				 * 上一个节点必须是'主管模式'
				 */
				if (preFlowNode == null) {
					throw new PrintException("自选模式节点有误,请重新配置");
				}
				if (StringUtils.isBlank(preFlowNode.getType()) || !"manager".equalsIgnoreCase(preFlowNode.getType())) {
					throw new PrintException("自选模式只能在主管审批之后配置");
				}

				List<FlowVoAddEntityKV> users = entity.getUser();//当前节点审批人列表
				if (users == null || users.size() == 0) {
					throw new PrintException("步骤" + i + "的审批人不能为空");
				}

				if (users.size() == 1) {
					throw new PrintException("步骤" + i + "自定义模式必须选择多个审批人");
				}

				//添加审批节点
				FlowNode node = new FlowNode();
				node.setOrderNo(i);
				node.setFlowId(flow.getId());
				node.setType("optional");
				node.setManagerJson(JSONObject.toJSONString(users));
				flowNodeService.add(node);

				preFlowNode = node;
			}

			//其他
			else {
				throw new PrintException("审批类型有误" + type);
			}

		}
	}

	/**
	 * 查询审批流程列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 要搜索的组织ID列表
	 * @param keyword       要搜索的流程名称关键词
	 * @return
	 */
	@Override
	public List<FlowVoSelect> getFlowListByOrgAndDepartmentAndKeyword(@NotNull Integer orgId,
																	  List<Integer> departmentIds,
																	  String keyword) {
		if (orgId == null) {
			return null;
		}

		/**
		 * 查询审批流程列表
		 */
		SpringContextUtils.setPage();
		List<FlowVoSelect> flows = mapper.selectByOrgAndDepartmentAndKeyword(orgId, departmentIds, keyword);
		if (flows == null || flows.isEmpty()) {
			return null;
		}

		/**
		 * 组装返回值参数
		 */
		Map<Integer, User> tempUsers = new HashMap<>();
		Map<Integer, Department> tempDepartments = new HashMap<>();
		for (int i = 0; i < flows.size(); i++) {
			FlowVoSelect vo = flows.get(i);
			Integer flowId = vo.getId();

			/**
			 * 组装：审批人姓名
			 */
			List<FlowNode> flowNodes = flowNodeService.getByFlow(flowId);
			if (flowNodes != null && flowNodes.size() > 0) {
				for (int j = 0; j < flowNodes.size(); j++) {
					FlowNode flowNode = flowNodes.get(j);
					String type = flowNode.getType();
					String managerJson = flowNode.getManagerJson();
					if (StringUtils.isBlank(managerJson)) {
						continue;
					}

					List<FlowVoAddEntityKV> kvs = JSONObject.parseArray(managerJson, FlowVoAddEntityKV.class);
					if (kvs == null || kvs.isEmpty()) {
						continue;
					}

					for (int k = 0; k < kvs.size(); k++) {
						FlowVoAddEntityKV kv = kvs.get(k);
						String label = kv.getLabel();
						if (StringUtils.isNotBlank(type) && "manager".equalsIgnoreCase(type)) {
							vo.getManagers().add(label);
						} else {
							int userId = kv.getKey();
							User user = getUser(tempUsers, userId);
							if (user != null) {
								vo.getManagers().add(user.getUserName());
							}
						}
					}
				}
			}

			/**
			 * 组装：发起人姓名
			 */
			Integer userId = vo.getUserId();
			User user = getUser(tempUsers, userId);
			if (user != null) {
				vo.setUserName(user.getUserName());
			}

			/**
			 * 组装：归属组织列表
			 */
			List<Integer> flowDepartmentIds = relateFlowDepartmentService.getDepartmentByFlow(flowId);
			if (flowDepartmentIds == null || flowDepartmentIds.isEmpty()) {
				continue;
			}
			for (int j = 0; j < flowDepartmentIds.size(); j++) {
				Integer departmentId = flowDepartmentIds.get(j);
				Department department = getDepartment(tempDepartments, departmentId);
				if (department != null) {
					String name = department.getName();
					vo.getDepartmentNames().add(name);
				}
			}
		}

		return flows;
	}

	/**
	 * 从临时容器中查，查不到再条件其他系统查询
	 *
	 * @param tempDepartments
	 * @param departmentId
	 * @return
	 */
	private Department getDepartment(Map<Integer, Department> tempDepartments, Integer departmentId) {
		Department department = tempDepartments.get(departmentId);
		if (department == null) {
			department = departmentService.get(departmentId);
			if (department != null && department.getId() != null) {
				tempDepartments.put(departmentId, department);
			}
		}
		return department;
	}

	/**
	 * 从临时容器中查，查不到再条件其他系统查询
	 *
	 * @param tempUsers
	 * @param userId
	 * @return
	 */
	private User getUser(Map<Integer, User> tempUsers, Integer userId) {
		User user = tempUsers.get(userId);
		if (user == null) {
			user = userService.get(userId);
			if (user != null && user.getId() != null) {
				tempUsers.put(userId, user);
			}
		}
		return user;
	}

	/**
	 * 查询审批流程列表
	 *
	 * @param orgId        集团ID
	 * @param departmentId 要搜索的组织ID
	 * @return
	 */
	@Override
	public List<FlowEntity> getByOrgAndDepartment(@NotNull Integer orgId, @NotNull Integer departmentId) {
		if (orgId == null || departmentId == null) {
			return null;
		}
		SpringContextUtils.setPage();
		return mapper.selectByOrgAndDepartment(orgId, departmentId);
	}

	/**
	 * 更新审批流程
	 *
	 * @param userInfo      操作人
	 * @param flow          原审批流程
	 * @param name          新名称
	 * @param remark        新简介
	 * @param status        状态
	 * @param flowNodeList  审批节点列表
	 * @param departmentIds 组织ID列表
	 */
	@Override
	@Transactional
	public void updateFlow(@NotNull UserInfo userInfo,
						   @NotNull Flow flow,
						   @NotNull String name,
						   String remark,
						   @NotNull Integer status,
						   @NotNull List<FlowVoAddEntity> flowNodeList,
						   List<Integer> departmentIds) {
		flow.setName(name);
		flow.setRemark(remark);
		flow.setStatus(status);

		//删除该审批流程对应的所有审批节点
		List<FlowNode> flowNodes = flowNodeService.getByFlow(flow.getId());
		if (flowNodes != null && flowNodes.size() > 0) {
			for (int i = 0; i < flowNodes.size(); i++) {
				FlowNode node = flowNodes.get(i);
				flowNodeService.del(node);
			}
		}

		//更新审批流程
		update(flow);

		//添加新流程步骤列表
		createFlowNode(flowNodeList, flow);

		//删除原组织关联列表信息
		List<RelateFlowDepartment> rfds = relateFlowDepartmentService.getByFlow(flow.getId());
		if (rfds != null && rfds.size() > 0) {
			for (int i = 0; i < rfds.size(); i++) {
				RelateFlowDepartment rfd = rfds.get(i);
				relateFlowDepartmentService.del(rfd);
			}
		}

		//添加组织关联信息
		for (Integer departmentId : departmentIds) {
			RelateFlowDepartment rfd = new RelateFlowDepartment();
			rfd.setDepartmentId(departmentId);
			rfd.setFlowId(flow.getId());

			relateFlowDepartmentService.add(rfd);
		}
	}

	/**
	 * 查询指定组织下的审批流程列表
	 *
	 * @param orgId         集团ID
	 * @param departmentIds 组织ID列表
	 * @return
	 */
	@Override
	public List<Flow> getByOrgAndDepartment(@NotNull Integer orgId, @NotNull List<Integer> departmentIds) {
		if (orgId == null || departmentIds == null || departmentIds.isEmpty()) {
			return null;
		}

		return mapper.selectByOrgAndDepartments(orgId, departmentIds);
	}
}
