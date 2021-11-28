package com.sci.labtrack.Lab;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sci.labtrack.PC.PC;
import com.sci.labtrack.R;

import java.util.ArrayList;

public class LabListAdapter extends BaseAdapter  {
   ArrayList<Lab> lablist = new ArrayList<>();
    LabsList l = new LabsList();
    public LabListAdapter(ArrayList<Lab> lablist, LabsList la) {
        this.lablist = lablist;
        l = la;
    }
    @Override
    public int getCount() {
        return lablist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v= LayoutInflater.from(l.getApplicationContext()).inflate(R.layout.labs_card,null);
        TextView name = v.findViewById(R.id.labname);
        TextView wpc = v.findViewById(R.id.workingpc);
        TextView nwpc = v.findViewById(R.id.notworkingpc);
        ImageView labImg = v.findViewById(R.id.labimg);
        TextView Total = v.findViewById(R.id.totalPcs);
        name.setText(lablist.get(position).getName());
        Total.setText(v.getContext().getString(R.string.Total)+lablist.get(position).PcList.size()+" /"+lablist.get(position).getMaxCap()+v.getContext().getString(R.string.pc));
        wpc.setText(v.getContext().getString(R.string.working)+howManyWorking((ArrayList<PC>) lablist.get(position).getPcList())+v.getContext().getString(R.string.pc));
        nwpc.setText(v.getContext().getString(R.string.needrepair)+howManyNeedRepair((ArrayList<PC>) lablist.get(position).getPcList())+v.getContext().getString(R.string.pc));
        labImg.setImageResource(R.drawable.labimg);

        return v;
    }
    public int howManyNeedRepair(ArrayList<PC> pcs){
        int i = 0;
        for (PC pc :pcs) {
            if(pc.isNeedRepair()){
                i++;
            }
        }
        return i;
    }
    public int howManyWorking(ArrayList<PC> pcs){
        return pcs.size()-howManyNeedRepair(pcs);
    }
}
