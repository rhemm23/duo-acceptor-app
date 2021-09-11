package com.rh.duoacceptor;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.app.PendingIntent;
import android.app.Notification;

/*
 * Listens for incoming duo notifications and automatically approves them
 */
public class DuoNotificationListenerService extends NotificationListenerService {

    private static final String APPROVE = "Approve";
    private static final String DUO_PACKAGE_NAME = "com.duosecurity.duomobile";
    private static final String VIEW_MORE_ACTIONS = "Tap To View Actions";

    /*
     * Listens for duo notifications. In the case of one, checks if its asking to view more actions
     * If so, invokes that action. Otherwise if its asking to approve or deny the request, it automatically approves it
     */
    @Override
    public void onNotificationPosted(StatusBarNotification postedNotification) {
        if (postedNotification.getPackageName().equals(DUO_PACKAGE_NAME)) {
            Notification.Action[] actions = postedNotification.getNotification().actions;
            if (actions.length == 1 && actions[0].title.equals(VIEW_MORE_ACTIONS)) {
                invokeNotificationAction(actions[0]);
            } else if (actions.length == 2) {
                for (Notification.Action action : actions) {
                    if (action.title.equals(APPROVE)) {
                        invokeNotificationAction(action);
                    }
                }
            }
        }
    }

    /*
     * Attempts to invoke a notification action. Returns true if successful, false otherwise
     */
    private boolean invokeNotificationAction(Notification.Action action) {
        try {
            action.actionIntent.send();
        } catch (PendingIntent.CanceledException canceledException) {
            return false;
        }
        return true;
    }
}
