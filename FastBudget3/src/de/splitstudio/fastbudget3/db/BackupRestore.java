package de.splitstudio.fastbudget3.db;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.createChooser;
import static de.splitstudio.utils.activity.DialogHelper.createAlert;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.activity.DialogHelper;
import de.splitstudio.utils.db.Database;

public class BackupRestore {

	private static final String BACKUP_MIME_TYPE = "application/octet-stream";

	public static void createBackup(Context context) {
		File dest = getFile(context, ".backup");
		if (dest == null) {
			return;
		}

		backup(context, dest);
		sendFile(context, dest);
	}

	public static void createCsv(Context context, List<Category> categories) {
		File dest = getFile(context, ".csv");
		if (dest == null) {
			return;
		}

		if (createCsvFile(context, categories, dest)) {
			sendFile(context, dest);
		}
	}

	private static void sendFile(final Context context, final File src) {
		DialogHelper.createQuestion(context, R.string.success, R.string.backup_created, R.string.cancel,
			R.string.send_file, new Runnable() {
				@Override
				public void run() {
					String chooserTitle = context.getString(R.string.send_file);
					Intent intent = new Intent(ACTION_SEND);
					intent.setType(BACKUP_MIME_TYPE);
					intent.putExtra(EXTRA_STREAM, Uri.fromFile(src));
					context.startActivity(createChooser(intent, chooserTitle));
				}
			}, src.getAbsolutePath());
	}

	private static boolean createCsvFile(Context context, List<Category> categories, File dest) {
		String content = new CsvExport(categories).getContent();
		try {
			FileUtils.write(dest, content);
			return true;
		} catch (IOException e) {
			createAlert(context, R.string.error, R.string.error_write_file, R.string.ok);
			return false;
		}
	}

	private static void backup(final Context context, final File dest) {
		Database.getInstance(context).ext().backupSync(dest.getAbsolutePath());
	}

	private static File getFile(Context context, String suffix) {
		File externalFilesDir = context.getExternalFilesDir(null);
		if (externalFilesDir == null) {
			DialogHelper.createAlert(context, R.string.warning, R.string.error_no_external_storage, R.string.ok);
			return null;
		}
		String filename = context.getString(R.string.app_name) + suffix;
		return new File(externalFilesDir, filename);
	}

}
