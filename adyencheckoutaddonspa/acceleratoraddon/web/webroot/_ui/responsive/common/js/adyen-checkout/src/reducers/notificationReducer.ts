import {PayloadAction, RootAction} from "./rootReducer";
import {Notification} from "./types";
import {Action} from "redux";

export const notificationInitialState: Notification[] = []

export function notificationReducer(notifications: Notification[], action: RootAction): Notification[] {
    switch (action.type) {
        case "notifications/addNotification": {
            return [...notifications, action.payload]
        }
        case "notifications/removeAllNotifications":
            return []
        case "notifications/removeNotificationsWithoutRedirect": {
            return notifications.filter((notification) => notification.isRedirect)
        }
        case "notifications/removeNotificationsFromList": {
            return notifications.filter((notification) => !action.payload.includes(notification))
        }
        default:
            return notifications
    }
}


interface AddNotification extends PayloadAction<"notifications/addNotification", Notification> {
}

interface RemoveAllNotifications extends Action<"notifications/removeAllNotifications"> {
}

interface RemoveNotificationsWithoutRedirect extends Action<"notifications/removeNotificationsWithoutRedirect"> {
}

interface RemoveNotificationsFromList extends PayloadAction<"notifications/removeNotificationsFromList", Notification[]> {
}

export type NotificationAction =
    AddNotification
    | RemoveAllNotifications
    | RemoveNotificationsWithoutRedirect
    | RemoveNotificationsFromList