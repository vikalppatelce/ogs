package com.application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.RSAPublicKeySpec;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.application.beans.MessageObject;
import com.application.ui.activity.ContactActivity;
import com.digitattva.ttogs.R;

@SuppressLint("DefaultLocale")
public class Utilities {
	public static boolean isRTL = false;
	public static Point displaySize = new Point();
	private static int[] barPers = new int[2];
	private static final String TAG = Utilities.class.getSimpleName();

	public static void checkRTL() {
		Locale locale = Locale.getDefault();
		String lang = locale.getLanguage();
		if (lang == null) {
			lang = "en";
		}
		isRTL = lang.toLowerCase().equals("ar");
	}

	public static CharSequence highlight(String search, String originalText) {
		// ignore case and accents
		// the same thing should have been done for the search text
		search = search.toLowerCase();
		String normalizedText = Normalizer
				.normalize(originalText, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.toLowerCase();

		int start = normalizedText.indexOf(search);
		if (start < 0) {
			// not found, nothing to to
			return originalText;
		} else {
			// highlight each appearance in the original text
			// while searching in normalized text
			Spannable highlighted = new SpannableString(originalText);
			while (start >= 0) {
				int spanStart = Math.min(start, originalText.length());
				int spanEnd = Math.min(start + search.length(),
						originalText.length());

				highlighted.setSpan(new BackgroundColorSpan(Color.YELLOW),
						spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				start = normalizedText.indexOf(search, spanEnd);
			}

			return highlighted;
		}
	}

	public static String getJabberGroupId(String mGroupJabberId) {
		return mGroupJabberId.substring(0, mGroupJabberId.indexOf("/"));
	}
	
	public static String getJabberGroupIdWithoutTale(String mGroupJabberId) {
		return mGroupJabberId.substring(0, mGroupJabberId.indexOf("@"));
	}

	public static String getJabberUserId(String mGroupJabberId) {
		return mGroupJabberId.substring(mGroupJabberId.indexOf("/") + 1,
				mGroupJabberId.length());
	}

	public static String getDeviceName() {
		try {
			String manufacturer = Build.MANUFACTURER;
			String model = Build.MODEL;
			if (model.startsWith(manufacturer)) {
				return capitalize(model);
			} else {
				return capitalize(manufacturer) + " " + model;
			}
		} catch (Exception e) {
			return "Device Unidentified";
		}
	}

	public static String capitalize(String s) {
		try {
			if (s == null || s.length() == 0) {
				return "";
			}
			char first = s.charAt(0);
			if (Character.isUpperCase(first)) {
				return s;
			} else {
				return Character.toUpperCase(first) + s.substring(1);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@SuppressLint("NewApi")
	public static double checkDisplaySize() {
		try {
			WindowManager manager = (WindowManager) ApplicationLoader.applicationContext
					.getSystemService(Context.WINDOW_SERVICE);
			if (manager != null) {
				Display display = manager.getDefaultDisplay();
				if (display != null) {
					if (android.os.Build.VERSION.SDK_INT < 13) {
						displaySize
								.set(display.getWidth(), display.getHeight());
					} else {
						display.getSize(displaySize);
					}
					FileLog.e("tmessages", "display size = " + displaySize.x
							+ " " + displaySize.y);
				}
			}
			return Math.sqrt((displaySize.x * displaySize.x)
					+ (displaySize.y * displaySize.y));
		} catch (Exception e) {
			FileLog.e("tmessages", e);
			return 0;
		}
	}

	public static int getTrueFalsePer(int trueCount, int falseCount,
			int trueAdd, int falseAdd) {
		int total = trueCount + falseCount + trueAdd + falseAdd;
		int truePer = 0;
		try {
			truePer = ((trueCount + trueAdd) * 100) / total;
		} catch (ArithmeticException ae) {
			Log.e(TAG, ae.toString());
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return truePer;
	}

	public static Uri getImagePath() {
		File imageDirectory = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		} else {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}
		imageDirectory.mkdirs();
		File tempFile = new File(imageDirectory, getVideoName()
				+ AppConstants.EXTENSION);
		return Uri.fromFile(tempFile);
	}

	public static Uri getVideoPath() {
		File imageDirectory = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);
		} else {
			imageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH_DATA);
		}

		imageDirectory.mkdirs();
		File tempFile = new File(imageDirectory, getVideoName()
				+ AppConstants.VIDEO_EXTENSION);
		return Uri.fromFile(tempFile);
	}

	public static String getVideoName() {
		String name = "Utilities";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			name = sdf.format(new Date(System.currentTimeMillis()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}

	public static void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	public static void galleryAddPic(Context mContext, Uri currentFileUri) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(currentFileUri.getPath());
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		mContext.sendBroadcast(mediaScanIntent);
	}

	public static boolean isInternetConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) ApplicationLoader.applicationContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}

	public static Bitmap getBitmapFromUri(Uri uri)
			throws FileNotFoundException, IOException {
		InputStream input = ApplicationLoader.applicationContext
				.getContentResolver().openInputStream(uri);
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inDither = true;
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
		input = ApplicationLoader.applicationContext.getContentResolver()
				.openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	public static String getDeviceId() {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader
					.getApplication().getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
			return telephonyManager.getDeviceId();
		} catch (Exception e) {
			return "No Device Id Found!";
		}
	}

	public static String getSDKVersion() {
		return String.valueOf(Build.VERSION.SDK_INT);
	}

	public static String getApplicationVersion() {
		PackageInfo pInfo = null;
		try {
			pInfo = ApplicationLoader
					.getApplication()
					.getApplicationContext()
					.getPackageManager()
					.getPackageInfo(
							ApplicationLoader.getApplication()
									.getApplicationContext().getPackageName(),
							0);
			return pInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "VERSION NAME NOT FOUND";
		}
	}

	public static String readFile(String s) {
		BufferedReader r;
		StringBuilder str = new StringBuilder();
		try {
			r = new BufferedReader(new InputStreamReader(ApplicationLoader
					.getApplication().getApplicationContext().getAssets()
					.open(s)));
			String line;
			while ((line = r.readLine()) != null) {
				str.append(line);
			}
		} catch (IOException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str.toString();
	}

	@SuppressWarnings("deprecation")
	public static int getDeviceWidth() {
		WindowManager wm = (WindowManager) ApplicationLoader.applicationContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth();
	}

	@SuppressWarnings("deprecation")
	public static int getDeviceHeight() {
		WindowManager wm = (WindowManager) ApplicationLoader.applicationContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getHeight();
	}

	public static int getDeviceGalleryFitWidth() {
		return (int) (getDeviceWidth() * 1.47);
	}

	public static String toHtml(Object object) {
		StringBuilder stringBuilder = new StringBuilder(256);
		try {
			for (Field field : object.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object val = field.get(object);
				stringBuilder.append("<b>");
				stringBuilder.append(field.getName().substring(1,
						field.getName().length()));
				stringBuilder.append(": ");
				stringBuilder.append("</b>");
				stringBuilder.append(val);
				stringBuilder.append("<br>");
			}
		} catch (Exception e) {
			// Do nothing
		}
		return stringBuilder.toString();
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}

	/**
	 * Convert Dp to Pixel
	 */
	public static int dpToPx(float dp, Resources resources) {
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				resources.getDisplayMetrics());
		return (int) px;
	}

	public static int getRelativeTop(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getTop();
		else
			return myView.getTop() + getRelativeTop((View) myView.getParent());
	}

	public static int getRelativeLeft(View myView) {
		// if (myView.getParent() == myView.getRootView())
		if (myView.getId() == android.R.id.content)
			return myView.getLeft();
		else
			return myView.getLeft()
					+ getRelativeLeft((View) myView.getParent());
	}

	public static Pattern pattern = Pattern.compile("[0-9]+");
	public static SecureRandom random = new SecureRandom();

	public static ArrayList<String> goodPrimes = new ArrayList<String>();

	public static class TPFactorizedValue {
		public long p, q;
	}

	public static volatile DispatchQueue stageQueue = new DispatchQueue(
			"stageQueue");
	public static volatile DispatchQueue globalQueue = new DispatchQueue(
			"globalQueue");
	public static volatile DispatchQueue searchQueue = new DispatchQueue(
			"searchQueue");
	public static volatile DispatchQueue photoBookQueue = new DispatchQueue(
			"photoBookQueue");

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static Integer parseInt(String value) {
		if (value == null) {
			return 0;
		}
		Integer val = 0;
		try {
			Matcher matcher = pattern.matcher(value);
			if (matcher.find()) {
				String num = matcher.group(0);
				val = Integer.parseInt(num);
			}
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return val;
	}

	public static String parseIntToString(String value) {
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			return matcher.group(0);
		}
		return null;
	}

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static byte[] hexToBytes(String hex) {
		int len = hex.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4) + Character
					.digit(hex.charAt(i + 1), 16));
		}
		return data;
	}

	public static boolean isGoodGaAndGb(BigInteger g_a, BigInteger p) {
		return !(g_a.compareTo(BigInteger.valueOf(1)) != 1 || g_a.compareTo(p
				.subtract(BigInteger.valueOf(1))) != -1);
	}

	public static boolean arraysEquals(byte[] arr1, int offset1, byte[] arr2,
			int offset2) {
		if (arr1 == null || arr2 == null
				|| arr1.length - offset1 != arr2.length - offset2
				|| arr1.length - offset1 < 0) {
			return false;
		}
		for (int a = offset1; a < arr1.length; a++) {
			if (arr1[a + offset1] != arr2[a + offset2]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] computeSHA1(byte[] convertme, int offset, int len) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(convertme, offset, len);
			return md.digest();
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static byte[] computeSHA1(ByteBuffer convertme, int offset, int len) {
		int oldp = convertme.position();
		int oldl = convertme.limit();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			convertme.position(offset);
			convertme.limit(len);
			md.update(convertme);
			return md.digest();
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		} finally {
			convertme.limit(oldl);
			convertme.position(oldp);
		}
		return null;
	}

	public static byte[] computeSHA1(ByteBuffer convertme) {
		return computeSHA1(convertme, 0, convertme.limit());
	}

	public static byte[] computeSHA1(byte[] convertme) {
		return computeSHA1(convertme, 0, convertme.length);
	}

	public static byte[] encryptWithRSA(BigInteger[] key, byte[] data) {
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(key[0], key[1]);
			PublicKey publicKey = fact.generatePublic(keySpec);
			final Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(data);
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static long bytesToLong(byte[] bytes) {
		return ((long) bytes[7] << 56) + (((long) bytes[6] & 0xFF) << 48)
				+ (((long) bytes[5] & 0xFF) << 40)
				+ (((long) bytes[4] & 0xFF) << 32)
				+ (((long) bytes[3] & 0xFF) << 24)
				+ (((long) bytes[2] & 0xFF) << 16)
				+ (((long) bytes[1] & 0xFF) << 8) + ((long) bytes[0] & 0xFF);
	}

	public static byte[] compress(byte[] data) {
		if (data == null) {
			return null;
		}

		byte[] packedData = null;
		ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
		try {
			GZIPOutputStream zip = new GZIPOutputStream(bytesStream);
			zip.write(data);
			zip.close();
			packedData = bytesStream.toByteArray();
		} catch (IOException e) {
			FileLog.e("tmessages", e);
		}
		return packedData;
	}

	public static boolean copyFile(InputStream sourceFile, File destFile)
			throws IOException {
		OutputStream out = new FileOutputStream(destFile);
		byte[] buf = new byte[4096];
		int len;
		while ((len = sourceFile.read(buf)) > 0) {
			Thread.yield();
			out.write(buf, 0, len);
		}
		out.close();
		return true;
	}

	public static boolean copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}
		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} catch (Exception e) {
			FileLog.e("tmessages", e);
			return false;
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
		return true;
	}

	public static String MD5(String md5) {
		if (md5 == null) {
			return null;
		}
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuilder sb = new StringBuilder();
			for (byte anArray : array) {
				sb.append(Integer.toHexString((anArray & 0xFF) | 0x100)
						.substring(1, 3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static void addMediaToGallery(String fromPath) {
		if (fromPath == null) {
			return;
		}
		File f = new File(fromPath);
		Uri contentUri = Uri.fromFile(f);
		addMediaToGallery(contentUri);
	}

	public static void addMediaToGallery(Uri uri) {
		if (uri == null) {
			return;
		}
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		mediaScanIntent.setData(uri);
		ApplicationLoader.applicationContext.sendBroadcast(mediaScanIntent);
	}

	private static File getAlbumDir() {
		File storageDir = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			storageDir = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"Telegram");
			if (storageDir != null) {
				if (!storageDir.mkdirs()) {
					if (!storageDir.exists()) {
						FileLog.d("tmessages", "failed to create directory");
						return null;
					}
				}
			}
		} else {
			FileLog.d("tmessages",
					"External storage is not mounted READ/WRITE.");
		}

		return storageDir;
	}

	@SuppressLint("NewApi")
	public static String getPath(final Uri uri) {
		try {
			final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
			if (isKitKat
					&& DocumentsContract.isDocumentUri(
							ApplicationLoader.applicationContext, uri)) {
				if (isExternalStorageDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];
					if ("primary".equalsIgnoreCase(type)) {
						return Environment.getExternalStorageDirectory() + "/"
								+ split[1];
					}
				} else if (isDownloadsDocument(uri)) {
					final String id = DocumentsContract.getDocumentId(uri);
					final Uri contentUri = ContentUris.withAppendedId(
							Uri.parse("content://downloads/public_downloads"),
							Long.valueOf(id));
					return getDataColumn(ApplicationLoader.applicationContext,
							contentUri, null, null);
				} else if (isMediaDocument(uri)) {
					final String docId = DocumentsContract.getDocumentId(uri);
					final String[] split = docId.split(":");
					final String type = split[0];

					Uri contentUri = null;
					if ("image".equals(type)) {
						contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}

					final String selection = "_id=?";
					final String[] selectionArgs = new String[] { split[1] };

					return getDataColumn(ApplicationLoader.applicationContext,
							contentUri, selection, selectionArgs);
				}
			} else if ("content".equalsIgnoreCase(uri.getScheme())) {
				return getDataColumn(ApplicationLoader.applicationContext, uri,
						null, null);
			} else if ("file".equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	public static File generatePicturePath() {
		try {
			File storageDir = getAlbumDir();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			return new File(storageDir, "IMG_" + timeStamp + ".jpg");
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static CharSequence generateSearchName(String name, String name2,
			String q) {
		if (name == null && name2 == null) {
			return "";
		}
		SpannableStringBuilder builder = new SpannableStringBuilder();
		String wholeString = name;
		if (wholeString == null || wholeString.length() == 0) {
			wholeString = name2;
		} else if (name2 != null && name2.length() != 0) {
			wholeString += " " + name2;
		}
		wholeString = wholeString.trim();
		String lower = " " + wholeString.toLowerCase();

		int index = -1;
		int lastIndex = 0;
		while ((index = lower.indexOf(" " + q, lastIndex)) != -1) {
			int idx = index - (index == 0 ? 0 : 1);
			int end = q.length() + (index == 0 ? 0 : 1) + idx;

			if (lastIndex != 0 && lastIndex != idx + 1) {
				builder.append(wholeString.substring(lastIndex, idx));
			} else if (lastIndex == 0 && idx != 0) {
				builder.append(wholeString.substring(0, idx));
			}

			String query = wholeString.substring(idx, end);
			if (query.startsWith(" ")) {
				builder.append(" ");
			}
			query.trim();
			builder.append(Html.fromHtml("<font color=\"#4d83b3\">" + query
					+ "</font>"));

			lastIndex = end;
		}

		if (lastIndex != -1 && lastIndex != wholeString.length()) {
			builder.append(wholeString.substring(lastIndex,
					wholeString.length()));
		}

		return builder;
	}

	public static File generateVideoPath() {
		try {
			File storageDir = getAlbumDir();
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
					.format(new Date());
			return new File(storageDir, "VID_" + timeStamp + ".mp4");
		} catch (Exception e) {
			FileLog.e("tmessages", e);
		}
		return null;
	}

	public static String formatFileSize(long size) {
		if (size < 1024) {
			return String.format("%d B", size);
		} else if (size < 1024 * 1024) {
			return String.format("%.1f KB", size / 1024.0f);
		} else if (size < 1024 * 1024 * 1024) {
			return String.format("%.1f MB", size / 1024.0f / 1024.0f);
		} else {
			return String.format("%.1f GB", size / 1024.0f / 1024.0f / 1024.0f);
		}
	}

	public static byte[] decodeQuotedPrintable(final byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		for (int i = 0; i < bytes.length; i++) {
			final int b = bytes[i];
			if (b == '=') {
				try {
					final int u = Character.digit((char) bytes[++i], 16);
					final int l = Character.digit((char) bytes[++i], 16);
					buffer.write((char) ((u << 4) + l));
				} catch (Exception e) {
					FileLog.e("tmessages", e);
					return null;
				}
			} else {
				buffer.write(b);
			}
		}
		return buffer.toByteArray();
	}

	public static String getAndroidOSVersion() {
		try {
			return android.os.Build.VERSION.RELEASE;
		} catch (Exception e) {
			return "Unknown OS";
		}
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDate(long currentMilliSeconds) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date resultdate = new Date(currentMilliSeconds);
			Log.i(TAG, "Date : "+sdf.format(resultdate));
			return sdf.format(resultdate);
		} catch (Exception e) {
			return "01/01/2020";
		}
	}
	
	public static long getMilliSecondsFromData(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		if(month==1){
			month = 12;
		}else{
			month--;
		}
		cal.set(year, month, day);
		return cal.getTimeInMillis();
	}
	
	public static long getMilliSecondsFromDataExpiry(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		/*if(month==1){
			month = 12;
		}else{
			month--;
		}
		cal.set(year, month, day);*/
		Date mDate = new Date(year, month, day);
		cal.setTime(mDate);
		return cal.getTimeInMillis();
	}
	

	@SuppressLint("SimpleDateFormat")
	public static String getTime(long currentMilliSeconds) {
		try {
			SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
			Date resultdate = new Date(currentMilliSeconds);
			return df.format(resultdate);
		} catch (Exception e) {
			return "00:00 am";
		}
	}
	
	public static Bitmap getBitmapFromImageView(ImageView image){
		return ((BitmapDrawable)image.getDrawable()).getBitmap();
	}
	
	public static String getEncodedImageToByteArray(Bitmap mBitmap){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		//this will convert image to byte[] 
		byte[] byteArrayImage = baos.toByteArray(); 
		// this will convert byte[] to string
		return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
	}
	
	public static String getEncodedFileToByteArray(String filePath) {
		String strFile = null;
		File file = new File(filePath);
		try {
			byte[] data = FileUtils.readFileToByteArray(file);
			strFile = Base64.encodeToString(data, Base64.NO_WRAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strFile;
	}
	
	
	public static void writeBitmapToSDCard(Bitmap mBitmap, String groudId, String messageTime){
		try{
			File mFileDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);    
			mFileDirectory.mkdirs();
			String mFileName = "Image-" + groudId + messageTime + ".jpg";
			File file = new File (mFileDirectory, mFileName);
			if (file.exists()){
				file.delete(); 
			}
			try {
			       FileOutputStream out = new FileOutputStream(file);
			       mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			       out.flush();
			       out.close();
			} catch (Exception e) {
			       e.printStackTrace();
			}
			
			try{
				String content ="TTOGS";
				File f = new File(mFileDirectory,".nomedia");
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			}catch(Exception e){
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}

	
	public static void writeBitmapToSDCardGroupImages(Bitmap mBitmap, String mGroupId){
		try{
			File mFileDirectory = new File(AppConstants.GROUP_IMAGE_DIRECTORY_PATH);    
			mFileDirectory.mkdirs();
			String mFileName = mGroupId+".jpg";
			File file = new File (mFileDirectory, mFileName);
			if (file.exists()){
				file.delete(); 
			}
			try {
			       FileOutputStream out = new FileOutputStream(file);
			       mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			       out.flush();
			       out.close();
			} catch (Exception e) {
			       e.printStackTrace();
			}
			try{
				String content ="TTOGS";
				File f = new File(mFileDirectory,".nomedia");
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			}catch(Exception e){
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public static void writeBitmapToSDCardAdsImages(Bitmap mBitmap, String mGroupId){
		try{
			File mFileDirectory = new File(AppConstants.ADS_DIRECTORY_PATH);    
			mFileDirectory.mkdirs();
			String mFileName = mGroupId;
			File file = new File (mFileDirectory, mFileName);
			if (file.exists()){
				file.delete(); 
			}
			try {
			       FileOutputStream out = new FileOutputStream(file);
			       mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			       out.flush();
			       out.close();
			} catch (Exception e) {
			       e.printStackTrace();
			}
			
			try{
				String content ="TTOGS";
				File f = new File(mFileDirectory,".nomedia");
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			}catch(Exception e){
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public static void writeBitmapToSDCardProfileImages(Bitmap mBitmap, String mGroupId){
		try{
			File mFileDirectory = new File(AppConstants.PROFILE_DIRECTORY_PATH);    
			mFileDirectory.mkdirs();
			String mFileName = mGroupId;
			File file = new File (mFileDirectory, mFileName);
			if (file.exists()){
				file.delete(); 
			}
			try {
			       FileOutputStream out = new FileOutputStream(file);
			       mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			       out.flush();
			       out.close();
			} catch (Exception e) {
			       e.printStackTrace();
			}
			try{
				String content ="TTOGS";
				File f = new File(mFileDirectory,".nomedia");
				FileWriter fw = new FileWriter(f.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();
			}catch(Exception e){
			}
		}catch(Exception e){
			Log.i(TAG, e.toString());
		}
	}
	
	public static String getFilePath(int messageType , String groupId, String messageTime){
		File mFileImageDirectory = new File(AppConstants.IMAGE_DIRECTORY_PATH);    
		mFileImageDirectory.mkdirs();
		
		File mFileAudioDirectory = new File(AppConstants.AUDIO_DIRECTORY_PATH);    
		mFileAudioDirectory.mkdirs();
		
		switch(messageType){
		case 0:
			return "";
		case 1:
			return AppConstants.IMAGE_DIRECTORY_PATH+"Image-"+ groupId + messageTime+".jpg";
		case 2:
			return AppConstants.AUDIO_DIRECTORY_PATH+"Audio-"+ groupId + messageTime+".3gp";
		}
		return "";
	}
	
	public static void cancelNotification(Context mContext) {
		NotificationManager mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(AppConstants.NOTIFICATION_ID);
	}

	public static void cancelLolliPopNotification(Context mContext) {
		try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				NotificationManager mNotificationManager = (NotificationManager) mContext
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(AppConstants.NOTIFICATION_ID);
			}
		} catch (Exception e) {
			Log.i(TAG, e.toString());
		}
	}
	
	public static String getFileNameFromPath(String FilePath){
		return FilePath.substring((FilePath.lastIndexOf("/") + 1),
				FilePath.length());
	}
	
	public static String getFileExtension(String FilePath){
		return FilePath.substring((FilePath.lastIndexOf(".") + 1),
				FilePath.length());
	}

	
	/*
	 * Extract Message 
	 */
	
	public static String getNotificationMessageText(MessageObject mMessageObject){
		String messageText;
		int messageType;
		try {
			messageText = mMessageObject.getMessageText();
			messageType = mMessageObject.getMessageType();
			switch(messageType){
			case 0:
				return messageText;
			case 1:
				return "image";
			case 2:
				return "audio";
			default:
					return "";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public static String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		
		// Convert total duration into time
		   int hours = (int)( milliseconds / (1000*60*60));
		   int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		   int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		   // Add hours if there
		   if(hours > 0){
			   finalTimerString = hours + ":";
		   }
		   
		   // Prepending 0 to seconds if it is one digit
		   if(seconds < 10){ 
			   secondsString = "0" + seconds;
		   }else{
			   secondsString = "" + seconds;}
		   
		   finalTimerString = finalTimerString + minutes + ":" + secondsString;
		
		// return timer string
		return finalTimerString;
	}
	
	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public static int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);
		
		// calculating percentage
		percentage =(((double)currentSeconds)/totalSeconds)*100;
		
		// return percentage
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public static int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);
		
		// return current duration in milliseconds
		return currentDuration * 1000;
	}
	
	
	public static boolean isUserAppExpires(){
		if(System.currentTimeMillis() > getMilliSecondsFromDataExpiry(Integer.parseInt(ApplicationLoader.getPreferences().getEndTime().substring(0, 4)),
						Integer.parseInt(ApplicationLoader.getPreferences().getEndTime().substring(5, 7)),
						Integer.parseInt(ApplicationLoader.getPreferences().getEndTime().substring(8, 10)))){
			return true;
		}
		return false;
	}
	
	public static void checkIfUserExpires(Activity mActivity) {
		// if(isUserAppExpires()){
		try {
//			if (isUserScriptionExpired(getSystemDateYYYYMMDD(),
//					ApplicationLoader.getPreferences().getEndTime())) {
				if(ApplicationLoader.getPreferences().getUserExpired()){
				showExpiredDialog(mActivity);
				}
				
				if(ApplicationLoader.getPreferences().getUserActiveStatus().equalsIgnoreCase("0")){
					showUserInactiveDialog(mActivity);
				}
				
		} catch (Exception e) {

		}
	}
	
	public static String getSystemDateYYYYMMDD(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(c.getTime());
	}
	
	public static long getCurrentNetworkTime() {
		final String TIME_SERVER = "time-a.nist.gov";
		Utilities.searchQueue.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				NTPUDPClient timeClient = new NTPUDPClient();
			    InetAddress inetAddress;
			    long returnTime = 0;
				try {
					inetAddress = InetAddress.getByName(TIME_SERVER);
					TimeInfo timeInfo = timeClient.getTime(inetAddress);
				    //long returnTime = timeInfo.getReturnTime();   //local device time
				    returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();   //server time
				    Date time = new Date(returnTime);
				    Log.d(TAG, "Time from " + TIME_SERVER + ": " + time);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		return 0;
	}
	
	public static void CheckAppUpdateAvailable(final Activity mActivity){
		Utilities.searchQueue.postRunnable(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(isInternetConnected()){
					try {
						String mResponseFromApi = RestClient.postJSON(AppConstants.API.URL, RequestBuilder.getPostAppUpdate());
						Log.i(TAG, mResponseFromApi);
						JSONObject mJSONObject = new JSONObject(mResponseFromApi);
						if(mJSONObject.getBoolean("success")){
							JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
//							if(Float.parseFloat(mJSONObjectData.getString("latest_app_version")) >  Float.parseFloat(getApplicationVersion())){
								if(mJSONObjectData.getBoolean("update")){
								showUpdateDialog(mActivity);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		});
	}
	
	
	public static boolean isUserScriptionExpired(String mAndroidDate, String mEndDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date1;
    	Date date2;
		try {
			date1 = sdf.parse(mAndroidDate);
			date2 = sdf.parse(mEndDate);

	    	System.out.println(sdf.format(date1));
	    	System.out.println(sdf.format(date2));

	    	if(date1.compareTo(date2)>0){
	    		System.out.println("Date1 is after Date2");
	    		return true;
	    	}else if(date1.compareTo(date2)<0){
	    		System.out.println("Date1 is before Date2");
	    		return false;
	    	}else if(date1.compareTo(date2)==0){
	    		System.out.println("Date1 is equal to Date2");
	    		return true;
	    	}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
	}
	
	
	public static boolean isToShowBannerAds(String mAndroidDate, String mStartDate, String mEndDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	Date date1;
    	Date date2;
    	Date date3;
		try {
			date1 = sdf.parse(mAndroidDate);
			date2 = sdf.parse(mStartDate);
			date3 = sdf.parse(mEndDate);
	    	if(date1.compareTo(date2)>0 && date1.compareTo(date3)<0){
	    		System.out.println("Date1 is after Date2");
	    		return true;
	    	}else if(date1.compareTo(date2)==0){
	    		System.out.println("Date1 is equal to Date2");
	    		return true;
	    	}else if(date1.compareTo(date3)==0){
	    		System.out.println("Date1 is equal to Date2");
	    		return true;
	    	}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return false;
	}
	
	
	public static void showUpdateDialog(final Activity mActivity){
		new AlertDialog.Builder(mActivity)
	    .setTitle(mActivity
	    		.getResources().getString(R.string.dialog_update_title))
	    .setMessage(mActivity
	    		.getResources().getString(R.string.dialog_update_message))
	    .setPositiveButton(mActivity
	    		.getResources().getString(R.string.dialog_update_positive), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	final String appPackageName = ApplicationLoader.getApplication().getResources().getResourceName(R.string.package_name); // getPackageName() from Context or Activity object
	        	try {
	        		mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
	        	} catch (android.content.ActivityNotFoundException anfe) {
	        		mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
	        	}
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}
	
	public static void showExpiredDialog(final Activity mActivity){
		new AlertDialog.Builder(mActivity)
	    .setTitle(mActivity
	    		.getResources().getString(R.string.dialog_expires_title))
	    .setMessage(mActivity
	    		.getResources().getString(R.string.dialog_expires_message))
	    .setPositiveButton(mActivity
	    		.getResources().getString(R.string.dialog_expires_positive), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	Intent mIntent = new Intent(mActivity, ContactActivity.class);
	        	mActivity.startActivity(mIntent);
	        	mActivity.finish();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}
	
	public static void showUserInactiveDialog(final Activity mActivity){
		new AlertDialog.Builder(mActivity)
	    .setTitle(mActivity
	    		.getResources().getString(R.string.dialog_inactive_title))
	    .setMessage(mActivity
	    		.getResources().getString(R.string.dialog_inactive_message))
	    .setPositiveButton(mActivity
	    		.getResources().getString(R.string.dialog_inactive_positive), new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        	Intent mIntent = new Intent(mActivity, ContactActivity.class);
	        	mActivity.startActivity(mIntent);
	        	mActivity.finish();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}
	
	public static void parseJSONUserInfo(String mResponseFromApi){
		try {
			JSONObject mJSONObject = new JSONObject(mResponseFromApi);
			if(mJSONObject.getBoolean("success")){
				JSONObject mJSONObjectData = mJSONObject.getJSONObject("data");
				JSONObject mJSONObjectUserInfo = mJSONObjectData.getJSONObject("user_info");
				ApplicationLoader.getPreferences().setEndTime(mJSONObjectUserInfo.getString("end_date"));
				ApplicationLoader.getPreferences().setStartTime(mJSONObjectUserInfo.getString("start_date"));
				ApplicationLoader.getPreferences().setUserActiveStatus(mJSONObjectUserInfo.getString("status"));
				ApplicationLoader.getPreferences().setUserId(mJSONObjectUserInfo.getString("user_id"));
				ApplicationLoader.getPreferences().setUserExpired(mJSONObjectUserInfo.getBoolean("is_expired"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("resource")
	public static void sendDBInMail(Context c) {
//		if(BuildVars.DEBUG_VERSION){
			try {
				File sd = Environment.getExternalStorageDirectory();

				if (sd.canWrite()) {
					String currentDBPath = "/data/data/"
							+ ApplicationLoader.getApplication().getPackageName()
							+ "/databases/ApplicationDB";
					String backupDBPath = "ApplicationDB.db_Dev.db";
					File currentDB = new File(currentDBPath);
					File backupDB = new File(sd, backupDBPath);

					if (currentDB.exists()) {
						FileChannel src = new FileInputStream(currentDB)
								.getChannel();
						FileChannel dst = new FileOutputStream(backupDB)
								.getChannel();
						dst.transferFrom(src, 0, src.size());
						src.close();
						dst.close();
						new MailTask(c, backupDB.getAbsolutePath()).execute();
					}
				}
			} catch (Exception e) {
			}
//		}
	}

	public static class MailTask extends AsyncTask<String, Void, String> {
		public Context mContext;
		public ProgressDialog pDialog;
		private String compressedPath;

		public MailTask(Context c, String compressedPath) {
			this.compressedPath = compressedPath;
			mContext = c;
		}

		@Override
		protected String doInBackground(String... params) {
			/** MAIL SENDING */
			Mail m = new Mail(BuildVars.EMAIL_USERNAME,
					BuildVars.EMAIL_PASSWORD);
			String[] toArr = { BuildVars.EMAIL_TO };
			m.setTo(toArr);
			m.setFrom(BuildVars.EMAIL_USERNAME);
			m.setSubject(BuildVars.EMAIL_SUBJECT);
			// m.setBody("<html><body><b><p>Dear Sir,"
			// + "  Following are the details added on Portfolio Application."
			// + "  Name:"+ _name +"  Contact No:"+_contact
			// +"  Address:"+_address+"</p><p> These is autogenerated mail. </p></b></body></html>");

			m.setBody(BuildVars.EMAIL_BODY);
			try {
				if (compressedPath != null && compressedPath.length() > 0)
					m.addAttachment(compressedPath);
				if (m.send()) {
					Log.e("MailApp", "Mail sent successfully!");
				} else {
					Log.e("MailApp", "Could not send email");
				}
			} catch (Exception e) {
				Log.e("MailApp", "Could not send email", e);
			}
			return "MailSent";
		}

		@Override
		protected void onPostExecute(String result) {
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}
}
