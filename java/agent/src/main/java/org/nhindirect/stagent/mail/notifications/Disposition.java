package org.nhindirect.stagent.mail.notifications;

public class Disposition
{

	private final TriggerType triggerType;
	private final SendType sendType;
	private NotificationType notification;
	private boolean error;
	
	public Disposition(NotificationType notification)
	{
		this(TriggerType.Automatic, SendType.Automatic, notification);
	}

	public Disposition(TriggerType triggerType, SendType sendType, NotificationType notification)
    {
        this.triggerType = triggerType;
        this.sendType = sendType;
        this.notification = notification;
        this.error = false;
    }

    public boolean isError() 
    {
		return error;
	}

	public void setError(boolean error) 
	{
		this.error = error;
	}

	public NotificationType getNotification() 
    {
		return notification;
	}

	public void setNotification(NotificationType notification) 
	{
		this.notification = notification;
	}

	public TriggerType getTriggerType() 
	{
		return triggerType;
	}

	public SendType getSendType() 
	{
		return sendType;
	}

	@Override
    public String toString()
    {
        StringBuilder notification = new StringBuilder();
        //
        // Disposition Mode
        //
        notification.append(NotificationHelper.asString(this.triggerType));
        notification.append('/');
        notification.append(NotificationHelper.asString(this.sendType));
        notification.append(';');
        //
        // Disposition Type & Modifier
        //
        notification.append(NotificationHelper.asString(this.notification));
        if (isError())
        {
            notification.append('/');
            notification.append(MDNStandard.Modifier_Error);
        }

        return notification.toString();
    }
}
