package org.fans.frame.fragment;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.fans.frame.activity.PhotoPickingActivity.ImageFileNameGenerator;
import org.fans.frame.utils.ImageUtils;
import org.fans.frame.utils.IoUtil;
import org.fans.frame.utils.ToastMaster;
import org.fans.frame.utils.Utils;
import org.fans.frame.utils.ViewUtils;
import org.fans.frame.widget.ActionSheet;
import org.fans.frame.widget.ActionSheet.OnButtonClickListener;
import org.fans.frame.widget.ActionSheetHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.fans.frame.R;

/**
 * 选取照片的基础类
 * 
 * 
 */
public abstract class PhotoPickingFragment extends NetworkFragment {
	private static final String TEMP_JPG = "temp.jpg";
	protected static final int TAKE_PHOTO = 1;
	protected static final int SELECT_FROM_ALBUMS = 2;
	protected static final int CROP_PIC = 3;
	//
	public static final int EFFECT_TYPE_CUT = 1;
	public static final int EFFECT_TYPE_NONE = 0;
	// 选取照片压缩
	private static final int SELECTED_PHOTO_COMPRESSED = 1012;
	// 拍照压缩
	private static final int TOOK_PHOTO_COMPRESSED = 1014;
	private int effecType;
	private int screenWidth;
	private int screenHeight;

	// 需要的文件地址
	private String needFilePath;

	// 拍照原地址
	private String imageCramePath;

	private String avatarLocal;

	private File tagFile;

	private int optType;

	public void requestTakePhoto(String title) {
		requestTakePhoto(title, EFFECT_TYPE_CUT, -1);
	}

	public void requestTakePhoto(String tilte, int effecType, int type) {
		Display display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
		setOptype(type);
		this.effecType = effecType;
		initDialogMenu(tilte);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {

			// File Filepath=FileDiskAllocator.creatBbsPostDir(this,
			// userInfo.getUserid());
			// String compressPath=Filepath.getAbsolutePath()+ "/" +
			// System.currentTimeMillis()+ ".jpg";
			// //自己拍照需要压缩
			// if (Filepath!=null) {
			// IoUtil.writeToFileCompress(bitmap, compressPath);
			// String thumbnailPath=Filepath.getAbsolutePath()+ "/" +
			// System.currentTimeMillis()+ ".jpg";
			if (requestCode == TAKE_PHOTO) {
				// 全图原始照片。。
				File path = null;
				if (imageCramePath != null) {
					path = getImgFilePath();
					// 全图压缩的地址
					// fileLocal = EncodeImageUtil.encodeImage(imageCramePath,
					// fileDir.toString(), "-1");
					// needFilePath = IoUtil.encodeImage(imageCramePath,
					// Filepath.toString());
					needFilePath = ImageUtils.compressNoLargePhoto(getActivity(), new File(imageCramePath), path, true);

					// NeedFilePath =
					// EncodeImageUtil.encodeImage(imageCramePath,
					// Filepath.toString(), "-1");
					if (effecType == EFFECT_TYPE_NONE) {
						onPhotoTaked(needFilePath, null);
					} else
						startPhotoZoom(Uri.fromFile(tagFile), "image");
					imageCramePath = null;
				}
			} else if (requestCode == SELECT_FROM_ALBUMS) {
				try {
					if (data == null) {
						return;
					}
					Uri uri = data.getData();
					AssetFileDescriptor fd = getActivity().getContentResolver().openAssetFileDescriptor(uri, "r");
					File fileDir = getImgFilePath();
					File realFile = new File(fileDir, System.currentTimeMillis() + ".jpg");
					IoUtil.writeToFile(realFile, fd.createInputStream());
					// needFilePath = IoUtil.encodeImage(realFile.toString(),
					// fileDir.toString());
					needFilePath = ImageUtils.compressNoLargePhoto(getActivity(), realFile, fileDir, true);

					if (effecType == EFFECT_TYPE_NONE) {
						onPhotoTaked(needFilePath, null);
					} else
						startPhotoZoom(data.getData(), "image");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (requestCode == SELECTED_PHOTO_COMPRESSED) {
				// 获得剪切过的小图，并压缩小图
				Bundle extras = null;
				if (data != null) {
					extras = data.getExtras();
				}
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);// (0
																			// -
																			// 100)压缩文件
					if (photo != null) {
						File fileDir = getImgFilePath();
						File realFile = new File(fileDir, System.currentTimeMillis() + ".jpg");
						avatarLocal = realFile.toString();
					}
					IoUtil.writeToFile(photo, avatarLocal);
				} else {
					return;
				}
				onPhotoTaked(needFilePath, avatarLocal);
				needFilePath = null;
				avatarLocal = null;
			} else if (requestCode == TOOK_PHOTO_COMPRESSED) {
				Bundle extras = null;
				if (data != null) {
					extras = data.getExtras();
				}
				if (extras != null) {
					Bitmap photo = extras.getParcelable("data");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.JPEG, 80, stream);// (0
																			// -
																			// 100)压缩文件
					File realFile = null;
					if (photo != null) {
						File fileDir = getImgFilePath();
						realFile = new File(fileDir, System.currentTimeMillis() + ".jpg");
					}
					avatarLocal = realFile.toString();
					IoUtil.writeToFile(photo, avatarLocal);
					onPhotoTaked(needFilePath, avatarLocal);

				}
			}
		}

	}

	public void startPhotoZoom(Uri uri, String tag) {

		int widthSize = ViewUtils.getDisplayMetrics(getActivity()).widthPixels;
		int heightSize = ViewUtils.getDisplayMetrics(getActivity()).heightPixels;
		int defSize;
		if (widthSize < heightSize)
			defSize = widthSize;
		else
			defSize = heightSize;

		defSize = 200;
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", defSize);
		intent.putExtra("outputY", defSize);
		intent.putExtra("return-data", true);
		if (tag.equals("image")) {
			startActivityForResult(intent, SELECTED_PHOTO_COMPRESSED);
		} else {
			startActivityForResult(intent, TOOK_PHOTO_COMPRESSED);
		}

	}

	public void onItemClick(int itemId) {
		switch (itemId) {
		case 1:
			selectFromAlbum();
			break;
		case 0:
			takePhoto();

			break;

		default:
			break;
		}
	}

	protected void onPhotoTaked(String picturePath, String smallPath) {

	}

	private void initDialogMenu(String title) {
		final ActionSheet sheet = (ActionSheet) ActionSheetHelper.createDialog(getActivity(), null);
		String[] items = null;
		items = getResources().getStringArray(R.array.detail_camer_pic);
		sheet.addButton(items[0], ActionSheet.WHITE_STYLE_BTN);
		sheet.addButton(items[1], ActionSheet.WHITE_STYLE_BTN);
		sheet.setMainTitle(title);
		sheet.setOnButtonClickListener(new OnButtonClickListener() {

			@Override
			public void onClick(View clickedView, int which) {
				onItemClick(which);
				sheet.dismiss();

			}
		});
		sheet.addCancelButton(R.string.cancle);
		sheet.show();
	}

	protected Bitmap getResizedPicture(String picturePath) {
		Bitmap bitmap = ImageUtils.decodeFile(picturePath, screenWidth, screenHeight, Config.RGB_565);
		if (bitmap != null && (bitmap.getWidth() > screenWidth || bitmap.getHeight() > screenHeight)) {
			bitmap = ImageUtils.zoomBitmap(bitmap, screenWidth, screenHeight);
		}
		return bitmap;
	};

	public void takePhoto() {
		if (Utils.checkSdCardAvailable()) {

			// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// File outputFileDir = FileDeskAllocator.allocateAvaterDir(
			// userInfoSetUI.this, true, MessagesReceiveService.lognName);
			// if (outputFileDir == null)
			// return;
			// tagFile = new File(outputFileDir, System.currentTimeMillis() +
			// ".jpg");
			// Uri outputFileUri = Uri.fromFile(tagFile);
			// imageCramePath = outputFileUri.getPath();
			// intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			// startActivityForResult(intent, HEAD_IMAGE_TACK_PICTURE);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File Filepath = getImgFilePath();
			tagFile = new File(Filepath, System.currentTimeMillis() + ".jpg");
			Uri outputFileUri = Uri.fromFile(tagFile);
			imageCramePath = outputFileUri.getPath();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
			// File tempFile = new File(FansConstasts.CACHE_DIR, TEMP_JPG);
			// tempFile.delete();
			// Uri tempUri = Uri.fromFile(tempFile);
			// intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			startActivityForResult(intent, TAKE_PHOTO);
		} else {

			ToastMaster.popToast(getActivity(), "请插入SD卡");
		}
	}

	public void selectFromAlbum() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// "image/*");
		// }
		intent.setType("image/*");
		startActivityForResult(intent, SELECT_FROM_ALBUMS);
	}

	public void setOptype(int type) {
		this.optType = type;
	}

	private File getImgFilePath() {
		String fileName = imageFileNameGenerator != null ? imageFileNameGenerator.generateName(optType) : TEMP_JPG;
		return Utils.getDiskCachePath(getActivity(), fileName);

	}

	private ImageFileNameGenerator imageFileNameGenerator;

	public void setImageFileNameGenerator(ImageFileNameGenerator imageFileNameGenerator) {
		this.imageFileNameGenerator = imageFileNameGenerator;
	}



}
