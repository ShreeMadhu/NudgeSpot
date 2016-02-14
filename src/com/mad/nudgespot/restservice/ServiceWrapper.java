package com.mad.nudgespot.restservice;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

public class ServiceWrapper {

	public static final int IO_EXCEPTION_ERROR = 1;
	public static final int FILE_NOT_FOUND = 2;
	public static final int MALFORMED_URL_ERROR = 3;
	public static final int SERVICE_CONNECTION_ERROR = 4;
	public static final int SERVICE_DATA_SEND_ERROR = 5;

	private Handler uploadHandler;
	private Context context;

	public ServiceWrapper(Handler uploadHandler, Context context) {
		this.uploadHandler = uploadHandler;
		this.context = context;
	}

	public enum ServiceRequestProtocol {
		REST
	};

	public enum ServiceRequestMethod {
		GET, POST, PUT, DELETE
	};

	public class ServiceRequest {

		public ServiceRequest(String serviceUrl,
				ServiceRequestMethod requestMethod,
				ServiceRequestProtocol protocol, List<Object> pathParams,
				Map<String, String> queryParams,
				Map<String, String> requestHeaders, String requestBody) {

			Uri uri = Uri.parse(serviceUrl);

			Uri.Builder uriBuilder = uri.buildUpon();

			if (pathParams != null) {
				for (Object obj : pathParams) {
					uriBuilder.appendPath(obj.toString());
				}
			}

			if (queryParams != null) {
				for (Map.Entry<String, String> entry : queryParams.entrySet()) {
					uriBuilder.appendQueryParameter(entry.getKey(),
							entry.getValue());
				}
			}

			this.serviceUrl = uriBuilder.build().toString();
			this.requestMethod = requestMethod;

			this.requestBody = requestBody;
			this.requestHeaders = requestHeaders;
			this.protocol = protocol;
		}

		public ServiceRequest setCallBackData(Map<String, Object> data) {
			this.callbackData = data;

			return this;
		}

		public String serviceUrl;
		public ServiceRequestMethod requestMethod;
		public ServiceRequestProtocol protocol;
		public String requestBody;
		public Map<String, String> requestHeaders;
		public Map<String, Object> callbackData;
	}

	public class WebServiceResult {
		public String response;
		public String respCode;
		public int errorCode;
		public String errorString;
		public Exception Ex;
		public String serverDate;
		public int percentage;
		public int handlerResultCode;
		public ServiceRequest request;

		public WebServiceResult setPercentageProgress(int pcnt) {
			percentage = pcnt;
			return this;
		}

		public WebServiceResult setHandlerResultCode(int code) {
			handlerResultCode = code;
			return this;
		}

		public WebServiceResult setErrorMessage() {
			errorString = "";

			switch (errorCode) {
			case IO_EXCEPTION_ERROR:
			case FILE_NOT_FOUND:
				errorString = "1001";
				break;
			case MALFORMED_URL_ERROR:
				errorString = "1002";
				break;
			case SERVICE_CONNECTION_ERROR:
				errorString = "1003";
				break;
			case SERVICE_DATA_SEND_ERROR:
				errorString = "1004";
				break;
			default:
				errorString = "1005";

			}
			;
			return this;
		}
	}

	public void fetchData(ServiceRequest request) {
		new WebServiceAsync().execute(request);
	}

	private class WebServiceAsync extends
			AsyncTask<ServiceRequest, WebServiceResult, WebServiceResult> {

		private String TAG = WebServiceAsync.class.getName();

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(WebServiceResult... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(WebServiceResult result) {
			super.onPostExecute(result);
			if (result.Ex != null) {

				updateStatus(result.setErrorMessage().setHandlerResultCode(
						ServiceConstants.SERVICE_ERROR_HANDLER_CODE));

			} else {
				updateStatus(result
						.setPercentageProgress(100)
						.setHandlerResultCode(
								ServiceConstants.SERVICE_DATA_SUCCESS_HANDLER_CODE));
			}

		}

		@Override
		protected WebServiceResult doInBackground(ServiceRequest... params) {
			return doInBackground(params[0]);
		}

		private void updateStatus(WebServiceResult status) {

			publishProgress(status);
			if (uploadHandler != null) {
				switch (status.percentage) {
				case 25:
				case 40:
				case 50:
				case 68:
				case 76:
				case 82:
				case 90:
				case 100:
					if (status.Ex == null) {
						sendMessage(status);
					}
					break;
				}

				if (status.Ex != null) {
					sendMessage(status);
				}

			}
		}

		private void sendMessage(WebServiceResult status) {
			JSONObject obj = new JSONObject();
			try {
				obj.put(ServiceConstants.HANDLER_PERCENTAGE, status.percentage);
				obj.put(ServiceConstants.HANDLER_RESPONSE, status.response);
				obj.put(ServiceConstants.HANDLER_RESP_CODE, status.respCode);
				obj.put(ServiceConstants.HANDLER_SERVER_DATE, status.serverDate);

				if (status.Ex != null) {
					obj.put(ServiceConstants.HANDLER_EXCEPTION,
							status.Ex.getMessage());
					obj.put(ServiceConstants.HANDLER_ERROR_MSG,
							status.errorString);
					obj.put(ServiceConstants.HANDLER_ERROR_CODE,
							status.errorCode);
				}

				obj.put(ServiceConstants.HANDLER_SERVICE_URL,
						status.request.serviceUrl);

				Map<String, Object> callbackData = status.request.callbackData;
				if (callbackData != null) {
					for (Map.Entry<String, Object> entry : callbackData
							.entrySet()) {
						if (entry.getValue() != null) {
							obj.put(entry.getKey(), entry.getValue().toString());
						}
					}
				}

				Message message = new Message();
				message.what = status.handlerResultCode;
				message.obj = obj.toString();
				uploadHandler.handleMessage(message);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		protected WebServiceResult doInBackground(ServiceRequest input) {
			java.net.URL url = null;

			ServiceRequest request = input;
			WebServiceResult status = new WebServiceResult();
			status.handlerResultCode = ServiceConstants.SERVICE_PROGRESS_HANDLER_CODE;
			status.request = request;

			updateStatus(status.setPercentageProgress(60));

			String serviceUrl = request.serviceUrl;

			try {
				Log.e(TAG, "aServiceUrl= " + serviceUrl);
				url = new java.net.URL(serviceUrl);
			} catch (MalformedURLException e) {
				Log.e(TAG, "Bad URL", e);
				status.Ex = e;
				status.errorCode = MALFORMED_URL_ERROR;
				return status;
			}
			updateStatus(status.setPercentageProgress(68));
			processServiceRequest(url, request, status);

			return status;
		}

		private WebServiceResult processServiceRequest(java.net.URL url,
				ServiceRequest request, WebServiceResult status) {
			DataOutputStream dos = null;
			String requestBody = request.requestBody;
			ByteArrayOutputStream fos1 = null;
			InputStream is = null;
			InputStream inputStream = null;
			String serviceResponse = null;
			String serverDate = null;
			try {
				int httpResponseCode = 0;

				try {

					try {
						HttpURLConnection urlConnection = null;
						updateStatus(status.setPercentageProgress(72));
						try {
							Log.e(TAG, "before url.openConnection");
							urlConnection = (HttpURLConnection) url
									.openConnection();
						} catch (IOException e1) {
							Log.e(TAG, "Error opening connection", e1);
							status.Ex = e1;
							status.errorCode = SERVICE_CONNECTION_ERROR;
							return status;
						}
						updateStatus(status.setPercentageProgress(76));
						try {
							Log.e(TAG, "before setRequestMethod1");
							urlConnection
									.setRequestMethod(request.requestMethod
											.name());

							// urlConnection.setRequestProperty("Accept",
							// "*/*");
							urlConnection.setRequestProperty("Accept",
									"application/json");

							String authString = "api" + ":"
									+ "681247179aeff2d83d8dd0740a7191e1";

							byte[] data = authString.getBytes("UTF-8");
							String base64 = Base64.encodeToString(data,
									Base64.DEFAULT);

							urlConnection.setRequestProperty("Authorization",
									base64);

							if (request.protocol == ServiceRequestProtocol.REST) {
								urlConnection.setRequestProperty(
										"Content-Type", "application/json");
							}

							updateStatus(status.setPercentageProgress(79));
							urlConnection.setConnectTimeout(60 * 1000);
						} catch (ProtocolException e) {
							Log.e(TAG, "Error setting header", e);
							status.Ex = e;
							status.errorCode = SERVICE_DATA_SEND_ERROR;
							return status;
						}

						updateStatus(status.setPercentageProgress(82));
						// Write to the connection
						byte[] bytes = null;

						if (requestBody != null) {
							bytes = requestBody.getBytes();
						}

						if (bytes != null) {
							dos = new DataOutputStream(
									urlConnection.getOutputStream());
							dos.write(bytes);
						}

						updateStatus(status.setPercentageProgress(85));
						httpResponseCode = urlConnection.getResponseCode();

						updateStatus(status.setPercentageProgress(90));
						// Read the response
						is = new BufferedInputStream(
								urlConnection.getInputStream());

						byte[] resultBuffer = new byte[1024];
						int len1 = 0;

						fos1 = new ByteArrayOutputStream();
						updateStatus(status.setPercentageProgress(93));
						while ((len1 = is.read(resultBuffer)) > 0) {

							fos1.write(resultBuffer, 0, len1);

						}
						updateStatus(status.setPercentageProgress(95));
						serviceResponse = fos1.toString();

						Log.e(TAG, "^^^^^^^^^^**********^^^^^after resp= "
								+ serviceResponse);

						updateStatus(status.setPercentageProgress(97));

						Log.d(TAG, "Response code: " + httpResponseCode);

						releaseResources(dos, fos1, is);
					} catch (IOException e) {
						status.Ex = e;
						status.errorCode = SERVICE_DATA_SEND_ERROR;
						e.printStackTrace();
						return status;
					} finally {
						releaseResources(dos, fos1, is);
					}

				} catch (Exception e) {
					e.printStackTrace();
					status.Ex = e;
					return status;
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				status.response = serviceResponse;
				status.respCode = String.valueOf(httpResponseCode);
				status.serverDate = serverDate;
				return status;
			} finally {

				releaseResources(dos, fos1, is);
			}
		}

		private void releaseResources(DataOutputStream dos,
				ByteArrayOutputStream fos1, InputStream is) {
			if (fos1 != null) {
				try {
					fos1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				if (dos != null)
					dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
