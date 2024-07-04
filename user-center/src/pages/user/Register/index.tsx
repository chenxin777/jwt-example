import Footer from '@/components/Footer';
import {register} from '@/services/ant-design-pro/api';
import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {LoginForm, ProFormText,} from '@ant-design/pro-components';
import {message, Tabs} from 'antd';
import React, {useState} from 'react';
import {history} from 'umi';
import styles from './index.less';
import {SYSTEM_LOGO} from '@/constant';

const Register: React.FC = () => {
	const [type, setType] = useState<string>('account');
	
	// 表单提交
	const handleSubmit = async (values: API.RegisterParams) => {
		// 校验
		const {userPassword, checkPassword} = values;
		if (userPassword !== checkPassword) {
			message.error('两次密码不一致！');
			return;
		}
		try {
			// 注册
			const id = await register(values);
			if (id) {
				const defaultLoginSuccessMessage = '注册成功！';
				message.success(defaultLoginSuccessMessage);
				
				/** 此方法会跳转到 redirect 参数所在的位置 */
				if (!history) return;
				const {query} = history.location;
				history.push({
					pathname: '/user/login',
					query,
				});
				return;
			}
		} catch (error: any) {
			const defaultLoginFailureMessage = '注册失败，请重试！';
			message.error(defaultLoginFailureMessage);
		}
	};
	return (
		<div className={styles.container}>
			<div className={styles.content}>
				<LoginForm
					submitter={{
						searchConfig: {
							submitText: '注册',
						},
					}}
					logo={<img alt="logo" src={SYSTEM_LOGO}/>}
					title="玩物志"
					subTitle={'面朝大海 春暖花开'}
					initialValues={{
						autoLogin: true,
					}}
					onFinish={async (values) => {
						await handleSubmit(values as API.LoginParams);
					}}
				>
					<Tabs activeKey={type} onChange={setType}>
						<Tabs.TabPane key="account" tab={'账户密码注册'}/>
					</Tabs>
					{type === 'account' && (
						<>
							<ProFormText
								name="userAccount"
								fieldProps={{
									size: 'large',
									prefix: <UserOutlined className={styles.prefixIcon}/>,
								}}
								placeholder={'请输入账号'}
								rules={[
									{
										required: true,
										message: '账号是必填项！',
									},
								]}
							/>
							<ProFormText.Password
								name="userPassword"
								fieldProps={{
									size: 'large',
									prefix: <LockOutlined className={styles.prefixIcon}/>,
								}}
								placeholder={'请输入密码'}
								rules={[
									{
										required: true,
										message: '密码是必填项！',
									},
									{
										min: 8,
										type: 'string',
										message: '长度小于 8'
									}
								]}
							/>
							<ProFormText.Password
								name="checkPassword"
								fieldProps={{
									size: 'large',
									prefix: <LockOutlined className={styles.prefixIcon}/>,
								}}
								placeholder={'请再次输入密码'}
								rules={[
									{
										required: true,
										message: '密码是必填项！',
									},
									{
										min: 8,
										type: 'string',
										message: '长度小于 8'
									}
								]}
							/>
							<ProFormText
								name="planetCode"
								fieldProps={{
									size: 'large',
									prefix: <UserOutlined className={styles.prefixIcon}/>,
								}}
								placeholder={'请输入星球编号'}
								rules={[
									{
										required: true,
										message: '星球编号是必填项！',
									},
								]}
							/>
						</>
					)}
				</LoginForm>
			</div>
			<Footer/>
		</div>
	);
};
export default Register;
