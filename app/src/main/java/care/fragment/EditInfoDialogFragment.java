package care.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mtk.btnotification.R;

public class EditInfoDialogFragment extends DialogFragment{

	private EditInfoDialogListener mListener;
	private Activity activity;
	public interface EditInfoDialogListener{
		/**
		 * �ش�ֵ��activity
		 */
		public void onEditInfo(String content);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		mListener = (EditInfoDialogListener)activity;
	}
	
	public static EditInfoDialogFragment getIntance(final String content,final String title){
		EditInfoDialogFragment fragment = new EditInfoDialogFragment();
		final Bundle args = new Bundle();
		args.putString("content", content);
		args.putString("title", title);
		fragment.setArguments(args);
		
		return fragment;
	}
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		
		final Bundle args = getArguments();
		final String content = args.getString("content");
		final String title = args.getString("title");
		
		View view = LayoutInflater.from(activity).inflate(R.layout.nickname_dialog, null,false);
		Button test_button1 = (Button) view.findViewById(R.id.nick_button1);
		Button test_button2 = (Button) view.findViewById(R.id.nick_button2);
		TextView imei_text_id = (TextView) view.findViewById(R.id.imei_text_id);
		final EditText nick_edit = (EditText)view.findViewById(R.id.nick_edit); 
	
		imei_text_id.setText(title);
		nick_edit.setText(content);
		
		final Dialog aeftDialog = new AlertDialog.Builder(activity)
		.setView(view).create();
		
		test_button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String nickName = nick_edit.getText().toString().trim();
				mListener.onEditInfo(nickName);
				dismiss();
			}
			
		});
        test_button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
			
		});
        
        return aeftDialog;
	}
}
