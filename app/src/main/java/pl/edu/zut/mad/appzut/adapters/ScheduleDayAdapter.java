package pl.edu.zut.mad.appzut.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.edu.zut.mad.appzut.R;
import pl.edu.zut.mad.appzut.models.Schedule;

public class ScheduleDayAdapter extends RecyclerView.Adapter<ScheduleDayAdapter.ClassViewHolder> {

    private final Context context;
    private List<Schedule.Hour> hoursInDay;

    public ScheduleDayAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ClassViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_item, parent, false);
        return new ClassViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClassViewHolder holder, int position) {
        if(position % 2 != 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.backgroundGray));
            holder.timeTextView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }
        Schedule.Hour hour = hoursInDay.get(position);
        holder.timeTextView.setText(hour.getStartTime());
        holder.nameTypeTextView.setText(hour.getSubjectNameWithType());
        holder.roomLecturerTextView.setText(hour.getLecturerWithRoom());
    }

    @Override
    public int getItemCount() {
        return hoursInDay == null ? 0 : hoursInDay.size();
    }

    public void setHoursInDay(List<Schedule.Hour> hoursInDay) {
        this.hoursInDay = hoursInDay;
        notifyDataSetChanged();
    }

    static class ClassViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_view) View itemView;
        @BindView(R.id.time) TextView timeTextView;
        @BindView(R.id.name_type) TextView nameTypeTextView;
        @BindView(R.id.room_lecturer) TextView roomLecturerTextView;

        ClassViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
