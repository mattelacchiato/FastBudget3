package de.splitstudio.fastbudget3.db;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_STREAM;
import static android.content.Intent.createChooser;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import de.splitstudio.fastbudget3.R;
import de.splitstudio.utils.activity.DialogHelper;
import de.splitstudio.utils.db.Database;

public class BackupRestore {

	private static final String BACKUP_MIME_TYPE = "application/octet-stream";

	public static void backup(final Context context) {
		File externalFilesDir = context.getExternalFilesDir(null);
		if (externalFilesDir == null) {
			DialogHelper.createAlert(context, R.string.warning, R.string.warning_no_external_storage, R.string.ok);
		} else {
			String filename = context.getString(R.string.app_name) + ".backup";
			final File dest = new File(externalFilesDir, filename);
			Database.getInstance(context).ext().backupSync(dest.getAbsolutePath());
			DialogHelper.createQuestion(context, R.string.success, R.string.warning_backup_created, R.string.cancel,
				R.string.send_file, new Runnable() {
					@Override
					public void run() {
						String chooserTitle = context.getString(R.string.send_file);
						Intent intent = new Intent(ACTION_SEND);
						intent.setType(BACKUP_MIME_TYPE);
						intent.putExtra(EXTRA_STREAM, Uri.fromFile(dest));
						context.startActivity(createChooser(intent, chooserTitle));
					}
				}, dest.getAbsolutePath());
		}
	}

}
