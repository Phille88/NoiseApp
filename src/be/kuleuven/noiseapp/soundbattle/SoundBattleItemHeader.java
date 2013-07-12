package be.kuleuven.noiseapp.soundbattle;

import be.kuleuven.noiseapp.R;
import be.kuleuven.noiseapp.soundbattle.SoundBattleItemAdapter.RowType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SoundBattleItemHeader implements iSoundBattleListItem {

    private final String name;
	
	public SoundBattleItemHeader(String name){
		this.name = name;
	}

	@Override
	public int getViewType() {
		return RowType.HEADER_ITEM.ordinal();
	}

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_item_soundbattles_header, null);
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.txt_soundbattles_header);
        text.setText(name);

        return view;
    }

}
