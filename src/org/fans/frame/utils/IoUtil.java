package org.fans.frame.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.fans.frame.FansApplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * IO 操作类
 * 
 * @author bsn
 * 
 * 
 */
public class IoUtil {
	public static void writeToFile(final Bitmap b, final String fileName) {
		OutputStream out = null;
		try {
			File file = new File(fileName);
			out = new FileOutputStream(file);
			b.compress(CompressFormat.JPEG, 100, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	// public static String encodeImage(String path, String fileDir) {
	//
	// try {
	// // 压缩图片
	// BitmapFactory.Options opts = new BitmapFactory.Options();
	// opts.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(path, opts);
	// opts.inSampleSize = ComputeSampleSizeOOM.computeSampleSize(opts, -1,
	// 1024*968);
	// opts.inJustDecodeBounds = false;
	// opts.inPreferredConfig=Bitmap.Config.RGB_565;
	// opts.inPurgeable=true;
	// opts.inInputShareable=true;
	// Bitmap bgimage=null;
	// try {
	// bgimage = BitmapFactory.decodeFile(path, opts);
	// } catch (OutOfMemoryError e) {
	// return "";
	// // TODO: handle exception
	// }
	// if (bgimage == null)
	// return "";
	// //旋转图片
	// int round = ImageUtils.readPictureDegree(path);
	// if(round != 0){
	// bgimage = ImageUtils.rotation(bgimage, round);
	// }
	// // 获取这个图片的宽和高
	// int width = bgimage.getWidth();
	// int height = bgimage.getHeight();
	// // 计算缩放率，新尺寸除原始尺寸
	// float scalMin = ((float) 640) / (height > width ? height : width);//
	// scaleWidth<scaleHeight?scaleWidth:scaleHeight;
	// Bitmap bitmap = Bitmap.createScaledBitmap(bgimage,
	// (int) (width * scalMin), (int) (height * scalMin), false);
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// // 得到输出流
	// bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
	// // 转输入流
	// InputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	// bitmap.recycle();
	// bgimage.recycle();
	// File sendFilePath = new File(fileDir, System.currentTimeMillis()
	// + ".jpg");
	// writeToFile(sendFilePath, isBm);
	// return sendFilePath.toString();
	//
	// } catch (Exception e) {
	// }
	// return "";
	// }

	public static void writeToFile(String fileName, byte[] data) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(fileName);
			out.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 推荐图片写入sd卡
	 * 
	 * @param sdPath
	 * @param mContext
	 */
	public static void SharewriteToSD(String sdPath, Context mContext) {
		try {

			File file = new File(sdPath);
			if (file.exists()) {
				// Toast.makeText(this, "SD卡已经存在文件", Toast.LENGTH_SHORT).show();

				return;
			}
			// 创建用于将图片保存到SD卡上的FileOutputStream对象
			FileOutputStream fos = new FileOutputStream(sdPath);
			// 打开assets目录下的image.jpg文件，并返回InputStream对象
			InputStream is = mContext.getResources().getAssets().open("share_img.png");
			// 定义一个byte数组，用来保存每次向SD卡中文件写入的数据，最多8k
			byte[] buffer = new byte[8192];
			int count = 0;
			// 循环写入数据
			while ((count = is.read(buffer)) != -1) {
				fos.write(buffer, 0, count);
			}
			fos.close();
			is.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static byte[] readFromInputStream(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buff = new byte[4096];
		int len = -1;
		while ((len = in.read(buff)) != -1) {
			bout.write(buff, 0, len);
		}
		return bout.toByteArray();
	}

	public static int buffLen = 1024;

	public static byte[][] readInputStreamToByteArray(File file) throws IOException {
		long totalLength = file.length();
		int length = (int) ((totalLength + buffLen - 1) / buffLen);
		Log.i("FILE_TRANSFER", "length=" + length);
		byte[][] buffs = new byte[length][];
		byte[] buff = new byte[buffLen];
		int len = -1;
		int index = 0;
		InputStream in = new FileInputStream(file);
		while ((len = in.read(buff)) != -1) {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(buff, 0, len);
			if (index > length)
				throw new RuntimeException("length=" + length + ",but index=" + index);
			if (len == buff.length) {
				buffs[index++] = buff;
			} else {
				byte[] tagBuff = new byte[len];
				System.arraycopy(buff, 0, tagBuff, 0, len);
				buffs[index++] = tagBuff;
			}
		}
		Log.i("FILE_TRANSFER", "buffs's length=" + buffs.length);
		return buffs;
	}

	/**
	 * ���ļ�ת����btye����
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

	public static String byteArrayToString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			buffer.append(bytes[i]);
		}
		return buffer.toString();
	}

	public static boolean writeToFile(File tagFile, InputStream in) {
		FileOutputStream fout = null;
		boolean success = true;
		try {
			fout = new FileOutputStream(tagFile);
			int len = -1;
			Log.i("SuccessUI", len + "");
			byte[] buff = new byte[4096];
			for (; (len = in.read(buff)) != -1;) {
				Log.i("SuccessUI", len + "");
				fout.write(buff, 0, len);
			}
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
		} finally {
			if (fout != null)
				try {
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return success;
	}

	// /** * Store image to SD card. */
	public static String storeImageToFile(Bitmap bitmap, File file) {
		if (bitmap == null) {
			return null;
		}
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
			return file.toString();
		} catch (IOException e) {
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public static byte[] transformToBytes(Bitmap b) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		b.compress(Bitmap.CompressFormat.PNG, 100, bout);
		return bout.toByteArray();
	}

	public static Drawable decodeByteArray(byte[] data) {
		Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
		return new BitmapDrawable(b);
	}

	/**
	 * 将图片压缩成最大边为960px的图片，以便于发送或上传到相册 如果最大边尺寸小于960px的，直接返回原图。
	 * 如果压缩后生成的图片文件比原图片占用的空间还大，则直接发送原图。
	 * 
	 * @param srcPath
	 *            源图片文件路径。
	 * @param maxEdgePixels
	 *            压缩后生成的最大边的像素数
	 * @param quality
	 *            压缩后图片的质量
	 * @return
	 */
	public static String compressPicToSend(String srcPath, String destPath, int quality) {
		String result = null;

		try {

			int maxEdgePixels = 960;
			File thumbFolder = FansApplication.getInstance().getCacheFolder();
			if (!thumbFolder.exists()) {
				thumbFolder.mkdir();
			}

			File destFile = new File(destPath);
			File srcFile = new File(srcPath);

			if (!srcFile.exists()) {
				return null;
			}

			if (!destFile.exists()) {
				destFile.createNewFile();
			}

			BitmapFactory.Options sizeOptions = getImageOptions(new FileInputStream(srcFile));
			int imgHeight = sizeOptions.outHeight;
			int imgWidth = sizeOptions.outWidth;

			// if (Math.max(imgHeight, imgWidth) <= maxEdgePixels) {
			// result = srcPath;
			// destFile.delete();
			// } else {
			float compRatio = (float) maxEdgePixels / Math.max(imgHeight, imgWidth);
			if (Math.max(imgHeight, imgWidth) <= maxEdgePixels) {
				compRatio = 1;
			}
			BitmapFactory.Options decodeOpt = new BitmapFactory.Options();
			decodeOpt.inSampleSize = Math.max(imgHeight, imgWidth) / maxEdgePixels;

			// 这里不再使用bitmapManager的decodeFile因为他在OOM的重试效果不好。
			Bitmap bitmapOrg = null;
			try {
				bitmapOrg = BitmapFactory.decodeFile(srcPath, decodeOpt);
			} catch (OutOfMemoryError oome) {
				bitmapOrg = null;
			}

			if (null == bitmapOrg)// X10
			{
				// QLog.d("compressImage", QLog.CLR,
				// "decode file null:"+srcPath);
				return null;
			}

			Bitmap outBmp = Bitmap.createScaledBitmap(bitmapOrg, (int) (compRatio * imgWidth), (int) (compRatio * imgHeight),
					false);
			int rotateDegree = ImageUtils.getRotateDegree(srcPath);
			if (rotateDegree != 0 && 0 == rotateDegree % 90) {
				float width = outBmp.getWidth();
				float height = outBmp.getHeight();

				Matrix m = new Matrix();
				m.setRotate(rotateDegree, width / 2, height / 2);
				outBmp = Bitmap.createBitmap(outBmp, 0, 0, (int) width, (int) height, m, true);
			}

			bitmapOrg = null;
			OutputStream os = null;
			try {

				os = new FileOutputStream(srcPath);
				// 存储
				outBmp.compress(CompressFormat.JPEG, quality, os);

				IoUtil.writeToFile(outBmp, destPath);

			} catch (Exception e) {
				result = null;
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
					}
				}
			}

			// 如果压缩后的图片文件的size大于原图片文件，则删除新生成的，返回srcPath；
			// 否则返回压缩后的图片文件。
			if (destFile.exists()) {
				if (destFile.length() < srcFile.length()) {
					result = destPath;
				} else {
					destFile.delete();
					result = srcPath;
				}
			} else {
				// 压缩过程中产生了exception，所以生成压缩图片失败
				result = srcPath;
			}
			// }
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (OutOfMemoryError oome) {
			System.gc();
		}
		return result;
	}

	/**
	 * 获取图片文件头信息
	 * 
	 * @param in
	 * @return
	 */
	public static BitmapFactory.Options getImageOptions(InputStream in) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, opts);
		return opts;
	}

}
