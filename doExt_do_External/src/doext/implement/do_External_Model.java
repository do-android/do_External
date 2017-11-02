package doext.implement;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import core.DoServiceContainer;
import core.helper.DoIOHelper;
import core.helper.DoJsonHelper;
import core.interfaces.DoIPage;
import core.interfaces.DoIScriptEngine;
import core.object.DoInvokeResult;
import core.object.DoSingletonModule;
import doext.define.do_External_IMethod;

/**
 * 自定义扩展SM组件Model实现，继承DoSingletonModule抽象类，并实现Do_Notification_IMethod接口方法；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象； 获取DoInvokeResult对象方式 new
 * DoInvokeResult(this.getUniqueKey());
 */
public class do_External_Model extends DoSingletonModule implements do_External_IMethod {

	public do_External_Model() throws Exception {
		super();
	}

	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		if ("openApp".equals(_methodName)) {
			openApp(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openURL".equals(_methodName)) {
			openURL(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openDial".equals(_methodName)) {
			openDial(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openContact".equals(_methodName)) {
			openContact(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}

		if ("openMail".equals(_methodName)) {
			openMail(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openSMS".equals(_methodName)) {
			openSMS(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("bulkSMS".equals(_methodName)) {
			bulkSMS(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("installApp".equals(_methodName)) {
			installApp(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openFile".equals(_methodName)) {
			openFile(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("openSystemSetting".equals(_methodName)) {
			openSystemSetting(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		if ("existApp".equals(_methodName)) {
			existApp(_dictParas, _scriptEngine, _invokeResult);
			return true;
		}
		return super.invokeSyncMethod(_methodName, _dictParas, _scriptEngine, _invokeResult);
	}

	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用， 可以根据_methodName调用相应的接口实现方法；
	 * 
	 * @throws Exception
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名 #如何执行异步方法回调？可以通过如下方法：
	 *                    _scriptEngine.callback(_callbackFuncName,
	 *                    _invokeResult);
	 *                    参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	 *                    获取DoInvokeResult对象方式new
	 *                    DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, JSONObject _dictParas, DoIScriptEngine _scriptEngine, String _callbackFuncName) throws Exception {
		return super.invokeAsyncMethod(_methodName, _dictParas, _scriptEngine, _callbackFuncName);
	}

	/**
	 * 启动其他应用；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openApp(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) {

		Intent _intent = new Intent();
		_intent.putExtra("__TYPE", "wakeup");
		Activity _activity = getContext(_scriptEngine);
		String _wakeupid = "";
		try {
			_wakeupid = DoJsonHelper.getString(_dictParas, "wakeupid", "");
			JSONObject _data = DoJsonHelper.getJSONObject(_dictParas, "data");
			if (_data != null) {
				Iterator<String> _keys = _data.keys();
				while (_keys.hasNext()) {
					String _key = _keys.next();
					String _value = _data.getString(_key);
					_intent.putExtra(_key, _value);
				}
			}

			// 通过包名启动其他应用
			PackageInfo _pi = _activity.getPackageManager().getPackageInfo(_wakeupid, 0);
			Intent _resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			_resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			_resolveIntent.setPackage(_pi.packageName);
			List<ResolveInfo> _apps = _activity.getPackageManager().queryIntentActivities(_resolveIntent, 0);
			ResolveInfo _ri = _apps.iterator().next();
			if (_ri != null) {
				ComponentName _cn = new ComponentName(_ri.activityInfo.packageName, _ri.activityInfo.name);
				_intent.setAction(Intent.ACTION_MAIN);
				_intent.addCategory(Intent.CATEGORY_LAUNCHER);
				_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				_intent.setComponent(_cn);
				_activity.startActivity(_intent);
			}
			_invokeResult.setResultBoolean(true);
		} catch (Exception _ex) {
			try {
				_intent.setAction(_wakeupid);
				_activity.startActivity(_intent);
				_invokeResult.setResultBoolean(true);
			} catch (Exception e) {
				_invokeResult.setResultBoolean(false);
			}
		}
	}

	/**
	 * 打开系统通讯录界面；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openContact(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		Intent _intent = new Intent(Intent.ACTION_VIEW);
		_intent.setType("vnd.android.cursor.dir/contact");
		_activity.startActivity(_intent);
	}

	/**
	 * 打开拨号界面；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openDial(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		String _number = DoJsonHelper.getString(_dictParas, "number", "");
		Intent _intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + _number));
		_activity.startActivity(_intent);
	}

	/**
	 * 打开发送邮件界面；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openMail(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		String _subject = DoJsonHelper.getString(_dictParas, "subject", "");
		String _content = DoJsonHelper.getString(_dictParas, "body", "");
		String _to = DoJsonHelper.getString(_dictParas, "to", "");

		Uri _uri = Uri.parse("mailto:" + _to);
		Intent _intent = new Intent(Intent.ACTION_SENDTO, _uri);
		_intent.putExtra(Intent.EXTRA_SUBJECT, _subject);
		_intent.putExtra(Intent.EXTRA_TEXT, _content);
		_activity.startActivity(_intent);
	}

	/**
	 * 打开发送短信界面；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openSMS(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		String _number = DoJsonHelper.getString(_dictParas, "number", "");
		String _body = DoJsonHelper.getString(_dictParas, "body", "");
		Uri _smsToUri = Uri.parse("smsto:" + _number);
		Intent _intent = new Intent(Intent.ACTION_SENDTO, _smsToUri);
		_intent.putExtra("sms_body", _body);
		_activity.startActivity(_intent);
	}

	/**
	 * 群发短信；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void bulkSMS(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		JSONArray _number = DoJsonHelper.getJSONArray(_dictParas, "number");
		if (_number == null || _number.length() <= 0) {
			throw new Exception("手机号不能为空！");
		}
		String _body = DoJsonHelper.getString(_dictParas, "body", "");
		String _mobile = "";
		for (int i = 0; i < _number.length(); i++) {
			_mobile += _number.getString(i) + ";";
		}
//		_mobile.substring(0, _mobile.length() - 1);
		Uri _smsToUri = Uri.parse("smsto:" + _mobile);
		Intent _intent = new Intent(Intent.ACTION_SENDTO, _smsToUri);
		_intent.putExtra("sms_body", _body);
		_activity.startActivity(_intent);
	}

	/**
	 * 调用系统默认浏览器；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openURL(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		String _url = DoJsonHelper.getString(_dictParas, "url", "");
		Intent _intent = new Intent(Intent.ACTION_VIEW, Uri.parse(_url));
		_activity.startActivity(_intent);
	}

	/**
	 * 调用系统安装界面安装apk；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void installApp(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		if (TextUtils.isEmpty(_path))
			throw new Exception("path不能为空！ ");
		String _fillPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
		if (!DoIOHelper.existFile(_fillPath))
			throw new Exception(_path + "文件不存在！ ");
		Activity _activity = getContext(_scriptEngine);
		Intent _intent = new Intent(Intent.ACTION_VIEW);
		_intent.setDataAndType(Uri.parse("file://" + _fillPath), "application/vnd.android.package-archive");
		_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		_activity.startActivity(_intent);
	}

	/**
	 * 打开拨号界面；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openSystemSetting(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		Activity _activity = getContext(_scriptEngine);
		String _type = DoJsonHelper.getString(_dictParas, "type", "");
		if (TextUtils.isEmpty(_type)) {
			throw new Exception("type参数不能为空！");
		}
		if ("GPS".equalsIgnoreCase(_type)) {
			Intent _intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
			_activity.startActivity(_intent);
			return;
		}
		throw new Exception("不支持的type类型为：" + _type);
	}

	/**
	 * 打开文件；
	 * 
	 * @_dictParas 参数（K,V），可以通过此对象提供相关方法来获取参数值（Key：为参数名称）；
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public void openFile(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _path = DoJsonHelper.getString(_dictParas, "path", "");
		try {
			if (TextUtils.isEmpty(_path)) {
				throw new RuntimeException("path不能为空！ ");
			}
			String _fillPath = _scriptEngine.getCurrentApp().getDataFS().getFileFullPathByName(_path);
			if (!DoIOHelper.existFile(_fillPath)) {
				throw new RuntimeException(_path + "文件不存在！ ");
			}
			Activity _activity = getContext(_scriptEngine);
			String end = _path.substring(_path.lastIndexOf("."), _path.length());
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setAction(Intent.ACTION_VIEW);
			// 获取文件file的MIME类型
			String type = getMIMEType(end);
			// 设置intent的data和Type属性。
			intent.setDataAndType(Uri.fromFile(new File(_fillPath)), type);
			_activity.startActivity(intent);
			_invokeResult.setResultBoolean(true);
		} catch (Exception e) {
			_invokeResult.setResultBoolean(false);
			DoServiceContainer.getLogEngine().writeError("openFile", e);
		}
	}

	private String getMIMEType(String dataType) {
		String type = "";
		for (int i = 0; i < MIME_MAPTABLE.length; i++) {
			if (dataType.equalsIgnoreCase(MIME_MAPTABLE[i][0])) {
				type = MIME_MAPTABLE[i][1];
				break;
			}
		}
		return type;
	}

	private static final String[][] MIME_MAPTABLE = {
			// {后缀名，MIME类型}
			{ ".doc", "application/msword" }, { ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" }, { ".xls", "application/vnd.ms-excel" },
			{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" }, { ".gif", "image/gif" }, { ".htm", "text/html" }, { ".html", "text/html" }, { ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" }, { ".tif", "image/tiff" }, { ".tiff", "image/tiff" }, { ".mp3", "audio/x-mpeg" }, { ".mp4", "video/mp4" }, { ".msg", "application/vnd.ms-outlook" },
			{ ".pdf", "application/pdf" }, { ".png", "image/png" }, { ".pps", "application/vnd.ms-powerpoint" }, { ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" }, { ".prop", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" }, { ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" },
			{ ".wps", "application/vnd.ms-works" }, { ".xml", "text/plain" }, { ".zip", "application/x-zip-compressed" }, { "", "*/*" } };

	@Override
	public void existApp(JSONObject _dictParas, DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult) throws Exception {
		String _key = DoJsonHelper.getString(_dictParas, "key", "");
		Activity _activity = getContext(_scriptEngine);
		_invokeResult.setResultBoolean(checkApplication(_key, _activity));
	}

	private Activity getContext(DoIScriptEngine _scriptEngine) {
		DoIPage _page = _scriptEngine.getCurrentPage();
		if (_page != null) {
			return (Activity) _page.getPageView();
		}
		return DoServiceContainer.getPageViewFactory().getAppContext();
	}

	public boolean checkApplication(String packageName, Activity _activity) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		try {
			_activity.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}
}