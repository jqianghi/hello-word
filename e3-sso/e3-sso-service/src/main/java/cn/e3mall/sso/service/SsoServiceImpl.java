package cn.e3mall.sso.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.po.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.manager.mapper.TbUserMapper;
import cn.e3mall.manager.po.TbUser;
import cn.e3mall.manager.po.TbUserExample;
import cn.e3mall.manager.po.TbUserExample.Criteria;
import redis.clients.jedis.JedisCluster;

@Service
public class SsoServiceImpl implements SsoService {

	@Autowired
	private TbUserMapper mapper;

	@Override
	public E3Result register(TbUser user) {

		// 其他校验
		if (user == null) {
			return E3Result.build(500, "用户信息不能为空");
		}
		if (StringUtils.isEmpty(user.getUsername())) {
			return E3Result.build(500, "用户名称不能为空");
		}
		if (StringUtils.isEmpty(user.getPassword())) {
			return E3Result.build(500, "用户密码不能为空");
		}
		if (StringUtils.isEmpty(user.getPhone())) {
			return E3Result.build(500, "用户手机号不能为空");
		}
		// 唯一性校验(用户名\手机号)
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(user.getUsername());
		List<TbUser> list = mapper.selectByExample(example);

		// 如果结果不为空,则返回
		if (list != null && list.size() > 0) {
			return E3Result.build(500, "用户名已经存在");
		}
		// 清空原来条件
		example.clear();

		Criteria criteria2 = example.createCriteria();
		criteria2.andPhoneEqualTo(user.getPhone());

		List<TbUser> list2 = mapper.selectByExample(example);
		// 如果结果不为空,则返回
		if (list2 != null && list2.size() > 0) {
			return E3Result.build(500, "用户手机号已经注册");
		}

		// 密码加密
		String md5password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		user.setPassword(md5password);
		// 属性补全
		user.setCreated(new Date());
		user.setUpdated(new Date());
		// 执行添加操作
		mapper.insert(user);
		return E3Result.ok();
	}

	@Autowired
	private JedisCluster cluster;

	@Value("${REDIS_SESSION_ID_PRE}")
	private String REDIS_SESSION_ID_PRE;

	@Value("${REDIS_SESSION_USER_KEY}")
	private String REDIS_SESSION_USER_KEY;

	@Value("${REDIS_SESSION_EXPIRE}")
	private int REDIS_SESSION_EXPIRE;

	@Override
	public E3Result login(String username, String password) {
		// 后台校验
		if (StringUtils.isEmpty(username)) {
			return E3Result.build(500, "用户名不能为空");
		}
		if (StringUtils.isEmpty(password)) {
			return E3Result.build(500, "密码不能为空");
		}
		// 登录校验(用户名\密码)
		// 判断用户名是否存在
		TbUserExample example = new TbUserExample();
		Criteria criteria = example.createCriteria();
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = mapper.selectByExample(example);

		if (list == null || list.size() != 1) {
			return E3Result.build(500, "用户名不正确");
		}

		TbUser user = list.get(0);
		// 判断密码是否一致
		String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
		if (!md5Password.equals(user.getPassword())) {
			return E3Result.build(500, "密码不正确");
		}
		// 生成token令牌(使用UUID生成)
		String token = UUID.randomUUID().toString();

		try {
			// 将登录成功的用户信息写入redis中(hash)
			cluster.hset(REDIS_SESSION_ID_PRE + token, REDIS_SESSION_USER_KEY, JsonUtils.objectToJson(user));
			// 设置有效期
			cluster.expire(REDIS_SESSION_ID_PRE + token, REDIS_SESSION_EXPIRE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 返回token
		return E3Result.ok(token);
	}

	@Override
	public E3Result checkLogin(String token) {
		// 判断token是否为空
		if (StringUtils.isEmpty(token)) {
			return E3Result.build(500, "用户未登录");
		}
		// 根据token值去redis中取出用户信息
		String userJson = cluster.hget(REDIS_SESSION_ID_PRE + token, REDIS_SESSION_USER_KEY);
		// 如果用户信息为空,说明没有登录
		if (StringUtils.isEmpty(userJson)) {
			return E3Result.build(500, "用户登录超时,请重新登录");
		}
		// 重新设置登录有效期
		cluster.expire(REDIS_SESSION_ID_PRE + token, REDIS_SESSION_EXPIRE);
		// 返回登录用户信息
		return E3Result.ok(JsonUtils.jsonToPojo(userJson, TbUser.class));
	}
}
