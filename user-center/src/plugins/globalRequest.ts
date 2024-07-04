import {extend} from 'umi-request';
import {history} from 'umi';
import {stringify} from 'querystring';
import {message} from 'antd';

const request = extend({
	prefix: process.env.NODE_ENV === 'production' ? 'http://8.140.27.137' : 'http://localhost:8080',
	
});

request.interceptors.request.use((url, options): any => {
	const token = localStorage.getItem('token');
	console.log(`do request url = ${url} with token: ${token}`);
	return {
		options: {
			...options,
			headers: {
				token: token,
			},
		}
	};
});

request.interceptors.response.use(async (response, options): Promise<any> => {
	const res = await response.clone().json();
	if (res.code === 0) {
		if (response.headers.get('token')) {
			// @ts-ignore
			localStorage.setItem('token', response.headers.get('token'));
		}
		return res.data;
	}
	// 未登录
	if (res.code === 40100) {
		message.error('请先登录');
		history.replace(
			{
				pathname: 'user/login',
				search: stringify({
					redirect: location.pathname,
				})
			}
		);
	} else {
		message.error(res.description);
	}
	return res.data;
});

export default request;
