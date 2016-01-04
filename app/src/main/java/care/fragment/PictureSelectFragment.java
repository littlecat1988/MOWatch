package care.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mtk.btnotification.R;

public class PictureSelectFragment extends DialogFragment{

	private PictureSelectInterface mListener;
	public interface PictureSelectInterface{
		
		public void onPictureSelectXiangji();
		
		public void onPictureSelectXiangce();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		this.mListener = (PictureSelectInterface)activity;
	}
	public static PictureSelectFragment getInstance(final Context context, final int title, final int select_string1,final int select_string2) {
		final PictureSelectFragment fragment = new PictureSelectFragment();

		final Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("select_string1", select_string1);
		args.putInt("select_string2", select_string2);

		fragment.setArguments(args);
		
		return fragment;
	}
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		
		final Bundle args = getArguments();
		final int select_string1 = args.getInt("select_string1");
		final int select_string2 = args.getInt("select_string2");
		final int title = args.getInt("title");
		
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.picture_select_fragment, null,false);
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(title);
		
		final AlertDialog dialogs = builder.setView(view).create();
		
		TextView select_xiangji = (TextView)view.findViewById(R.id.select_xiangji);
		select_xiangji.setText(select_string1);
		select_xiangji.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListener.onPictureSelectXiangji();
			}
		});
		TextView select_xiangce = (TextView)view.findViewById(R.id.select_xiangce);
		select_xiangce.setText(select_string2);
		select_xiangce.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mListener.onPictureSelectXiangce();
			}
		});
		return dialogs;
	}
}
