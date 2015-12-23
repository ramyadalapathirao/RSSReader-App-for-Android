/*Reference:
* http://www.marvinlabs.com/2010/10/29/custom-listview-ability-check-items/*/
package edu.sdsu.cs.ramya.rssreader;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;

public class CheckableRelativeLayout extends RelativeLayout implements Checkable
{

    private boolean isChecked;
    private List<Checkable> checkableViews;

    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialise(attrs);
    }

    public CheckableRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialise(attrs);
    }
    public CheckableRelativeLayout(Context context, int checkableId)
    {
        super(context);
        initialise(null);
    }

    private void initialise(AttributeSet attrs)
    {
        this.isChecked = false;
        this.checkableViews = new ArrayList<Checkable>(5);
    }

    @Override
    public void setChecked(boolean checked)
    {
        this.isChecked = checked;
        for (Checkable c : checkableViews) {
            c.setChecked(isChecked);
        }
    }

    @Override
    public boolean isChecked()
    {
        return isChecked;
    }

    @Override
    public void toggle() {
        this.isChecked = !this.isChecked;
        for (Checkable c : checkableViews) {
            c.toggle();
        }

    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        final int childCount = this.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            findCheckableChildren(this.getChildAt(i));
        }
    }
    private void findCheckableChildren(View v)
    {
        if (v instanceof Checkable)
        {
            this.checkableViews.add((Checkable) v);
        }

        if (v instanceof ViewGroup)
        {
            final ViewGroup vg = (ViewGroup) v;
            final int childCount = vg.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                findCheckableChildren(vg.getChildAt(i));
            }
        }
    }
}
