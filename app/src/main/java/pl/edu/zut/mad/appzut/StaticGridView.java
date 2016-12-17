package pl.edu.zut.mad.appzut;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * GridView optimized for Caldroid library
 * (which actually uses GridView in way GridView isn't supposed to)
 *
 * - Assumes all cells have same type and size
 * - Never scrolls
 */
public class StaticGridView extends GridView {

    /**
     * True when {@link #layoutChildren()} is executing
     */
    private boolean mBlockLayoutRequests;

    /**
     * Children views
     *
     * n-th view here always corresponds to n-th item in adapter and n-th child of this view
     *
     * If child is detached (that is we had it previously but it's not in adapter anymore)
     * it stays in this list
     *
     * Child is attached when it's index is < {@link #mAttachedChildrenCount}
     */
    private final List<View> mChildren = new ArrayList<>();

    /**
     * Number of items from {@link #mChildren} that are currently attached to this view
     *
     * Always <= mChildren.size() (Unless we're inside {@link #layoutChildren()}
     */
    private int mAttachedChildrenCount = 0;

    public StaticGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void layoutChildren() {
        // Prevent re entrance
        if (mBlockLayoutRequests) {
            return;
        }
        mBlockLayoutRequests = true;

        // Cell size variables
        boolean didMeasure = false;
        int cellWidth = 0;
        int cellHeight = 0;
        int cellWidthWithSpacing = 0;
        int cellHeightWithSpacing = 0;

        // Pre-cache values
        ListAdapter adapter = getAdapter();
        int adapterItemCount = adapter != null ? adapter.getCount() : 0;
        int numColumns = getNumColumns();

        int paddingLeft = getListPaddingLeft();
        int paddingTop = getListPaddingTop();

        // For each item
        for (int i = 0; i < adapterItemCount; i++) {
            View oldView = i < mChildren.size() ? mChildren.get(i) : null;
            View view = getAdapter().getView(i, oldView, this);

            // If View object has changed
            if (view != oldView) {
                // Put view in reuse list
                if (i < mChildren.size()) {
                    mChildren.set(i, view);
                } else {
                    mChildren.add(view);
                }

                // Setup events
                setupChild(view, i);

                // Remove old child if it has changed
                if (oldView != null && i < mAttachedChildrenCount) {
                    removeViewInLayout(oldView);
                }

                // Attach child to view
                addViewInLayout(view, i, view.getLayoutParams(), true);
            }

            if (!didMeasure) {
                view.measure(
                        MeasureSpec.makeMeasureSpec(getColumnWidth(), MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                );
                cellWidth = view.getMeasuredWidth();
                cellHeight = view.getMeasuredHeight();
                cellWidthWithSpacing = cellWidth + getVerticalSpacing();
                cellHeightWithSpacing = cellHeight + getHorizontalSpacing();
                didMeasure = true;
            } else {
                view.measure(
                        MeasureSpec.makeMeasureSpec(cellWidth, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(cellHeight, MeasureSpec.EXACTLY)
                );
            }

            int cellX = cellWidthWithSpacing * (i % numColumns) + paddingLeft;
            int cellY = cellHeightWithSpacing * (i / numColumns) + paddingTop;
            view.layout(cellX, cellY, cellX + cellWidth, cellY + cellHeight);
        }

        // Remove extraneous children
        if (adapterItemCount < mAttachedChildrenCount) {
            removeViewsInLayout(adapterItemCount, mAttachedChildrenCount - adapterItemCount);
        }

        // Remember which children are detached
        mAttachedChildrenCount = adapterItemCount;

        // Re-allow entrance
        mBlockLayoutRequests = false;
    }

    private void setupChild(final View child, final int i) {
        child.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performItemClick(child, i, getAdapter().getItemId(i));
            }
        });
    }
}
