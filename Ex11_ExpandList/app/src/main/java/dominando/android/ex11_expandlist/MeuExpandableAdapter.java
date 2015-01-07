package dominando.android.ex11_expandlist;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MeuExpandableAdapter extends BaseExpandableListAdapter {

    private Map<String, List<String>> dados;
    private List<String> keys;

    public MeuExpandableAdapter(Map<String, List<String>> dados) {

        this.dados = dados;
        this.keys = new ArrayList<String>(
                dados.keySet());
    }

    @Override
    public Object getGroup(int groupPosition) {
        return keys.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return dados.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition,
                             boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(
                    parent.getContext()).inflate(
                    android.R.layout.simple_expandable_list_item_1,
                    null);
        }
        TextView txt = (TextView)
                convertView.findViewById(android.R.id.text1);
        txt.setTextColor(Color.WHITE);
        txt.setBackgroundColor(Color.GRAY);
        txt.setText(keys.get(groupPosition));
        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return dados.get(
                keys.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition,
                             int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(
                    parent.getContext()).inflate(
                    android.R.layout.simple_list_item_1, null);
        }
        TextView txt = (TextView)
                convertView.findViewById(android.R.id.text1);
        txt.setText(dados.get(
                keys.get(groupPosition)).get(childPosition));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dados.get(keys.get(groupPosition)).size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(
            int groupPosition, int childPosition) {
        return true;
    }
}
