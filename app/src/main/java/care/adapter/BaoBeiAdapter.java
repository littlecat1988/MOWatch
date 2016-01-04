package care.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mtk.btnotification.R;

import java.util.ArrayList;

/**
 * Created by wid3344 on 2015/8/25.
 */
public class BaoBeiAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<String> mList;

    public BaoBeiAdapter(Context mContext){
        this.mInflater = LayoutInflater.from(mContext);
        this.mList = new ArrayList<String>();
    }

    public void addAll(ArrayList<String> mTempList){
        mList.addAll(mTempList);
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return mList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link android.view.LayoutInflater#inflate(int, android.view.ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.clock_item, parent, false);
            viewHolder.clockTime = (TextView) convertView.findViewById(R.id.text1);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        String temp = (String)getItem(position);
        viewHolder.clockTime.setText(temp);
        return convertView;
    }

    public void remove(int position) {
        if(position != -1){
            mList.remove(position);
            refresh();
        }
    }

    public void refresh(){
        notifyDataSetChanged();
    }

    public void add(String temp) {
        mList.add(temp);
        refresh();
    }

    public void setCurrentString(int position,String temp) {
        mList.set(position,temp);
        refresh();
    }

    private static final class ViewHolder{
        TextView clockTime;
    }
}
