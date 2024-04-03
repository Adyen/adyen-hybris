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
        default:
            return notifications
    }
}


interface AddNotification extends PayloadAction<"notifications/addNotification", Notification> {
}

interface RemoveAllNotifications extends Action<"notifications/removeAllNotifications"> {
}

export type NotificationAction =
    AddNotification
    | RemoveAllNotifications