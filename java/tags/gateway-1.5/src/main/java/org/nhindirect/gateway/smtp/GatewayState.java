/* 
Copyright (c) 2010, NHIN Direct Project
All rights reserved.

Authors:
   Umesh Madan     umeshma@microsoft.com
   Greg Meyer      gm2552@cerner.com
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
in the documentation and/or other materials provided with the distribution.  Neither the name of the The NHIN Direct Project (nhindirect.org). 
nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS 
BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE 
GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nhindirect.gateway.smtp;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nhindirect.gateway.smtp.config.SmtpAgentConfig;
import org.nhindirect.stagent.MutableAgent;
import org.nhindirect.stagent.NHINDAgent;

import com.google.inject.Injector;

/**
 * Manages the settings state of the gateway provides read/write lock protectors for concurrent operations.
 * <br>
 * This class implements a singleton pattern and is accessed by the static method {@link #getInstance()}.
 * @author Greg Meyer
 * @since 1.4
 */
public class GatewayState 
{
	 private static final Log LOGGER = LogFactory.getFactory().getInstance(GatewayState.class);
	
	 protected static final long DEFAULT_SETTINGS_UPDATE_DELAY = 300000; // 5 mintues
	
	 protected static GatewayState INSTANCE;

	 protected final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

	 protected SmtpAgent smtpAgent;
	 
	 protected SmtpAgentConfig smtpAgentConfig;
	 
	 protected SettingsManager settingsManager;
	 
	 protected long settingsThreadDelay = DEFAULT_SETTINGS_UPDATE_DELAY;
	 
	 /**
	  * Gets the singleton instance of the GatewayState class.
	  * @return The singleton instance of the GatewayState class.
	  */
	 public static synchronized GatewayState getInstance()
	 {
		 if (INSTANCE == null)
			 INSTANCE = new GatewayState();
			 
		 return INSTANCE;
	 }
	 
	 /*
	  * Private constructor
	  */
	 private GatewayState()
	 {
		 
	 }
	 
	 /**
	  * Locks a services for processing operations.  Semantically is the same as a {@link java.util.concurrent.locks.ReadWriteLock#readLock()}.  Operations using this lock should not attempt to change the state
	  * of the services they are consuming.   Multiple threads can concurrently obtain the processing lock and execute, however 
	  * this method will block is another thread holds the update lock.
	  */
	 public void lockForProcessing()
	 {
		 readWriteLock.readLock().lock();
	 }
	 
	 /**
	  * Unlocks a service from processing operations.  This releases the thread's access to the lock and allows access to the update lock if no
	  * other threads hold the processing lock.
	  */
	 public void unlockFromProcessing()
	 {
		 readWriteLock.readLock().unlock();
	 }
	 
	 /**
	  * Locks a service update operations.  Semantically is the same as a {@link java.util.concurrent.locks.ReadWriteLock#writeLock()}.  This lock is intended for updating
	  * the state of a service ensuring that the update does not have adverse affects on a processing operations.  Only a single thread can have access to the
	  * update thread at any given time.  This method will block if another thread hold the update lock or the processing lock.
	  */
	 public void lockForUpdating()
	 {
		 readWriteLock.writeLock().lock();
	 }
	 
	 /**
	  * Unlocks a service from update operations.  This release the thread's access to the lock.
	  */
	 public void unlockFromUpdating()
	 {
		 readWriteLock.writeLock().unlock();
	 }
	 
	 /**
	  * Sets the interval that the settings are updated when the {@link SettingsManager} is started.  If this interval is changed, the settings manager
	  * must be restarted before the new interval takes effect.
	  * @param intervalInSeconds The interval in seconds that the settings are updated.
	  */
	 public synchronized void setSettingsUpdateInterval(long intervalInSeconds)
	 {
		 this.settingsThreadDelay = intervalInSeconds * 1000;
	 }
	 
	 /**
	  * Gets the interval that the settings are update when the {@link SettingsManager} is started.
	  * @return he interval in seconds that the settings are updated.
	  */
	 public synchronized long getSettingsUpdateInterval()
	 {
		 return settingsThreadDelay / 1000;
	 }
	 
	 /**
	  * Sets the {@link SmtpAgent agent} whose state is managed.
	  * @param agent The {@link SmtpAgent agent} whose state is managed.
	  */
	 public synchronized void setSmtpAgent(SmtpAgent agent)
	 {
		 this.smtpAgent = agent;
	 }
	 
	 /**
	  * Gets the {@link SmtpAgent agent} whose state is being managed.
	  * @return The {@link SmtpAgent agent} whose state is being managed.
	  */
	 public synchronized SmtpAgent getSmtpAgent()
	 {
		 return this.smtpAgent;
	 }
	 
	 /**
	  * Sets the {@link SmtpAgentConfig config} object used to obtain agent settings and configuration.
	  * @param config The {@link SmtpAgentConfig config} object used to obtain agent settings and configuration.
	  */
	 public synchronized void setSmptAgentConfig(SmtpAgentConfig config)
	 {
		 this.smtpAgentConfig = config;
	 }
	 
	 /**
	  * Gets the {@link SmtpAgentConfig config} object that is being used to obtain agent settings and configuration.
	  * @return The {@link SmtpAgentConfig config} object that is being used to obtain agent settings and configuration.
	  */
	 public synchronized SmtpAgentConfig getSmtpAgentConfig()
	 {
		 return this.smtpAgentConfig;
	 }
	 
	 /**
	  * Starts the agent settings manager.  The manager updates the agent's setting as determined by the update interval (default is every five minutes).
	  * Changes to the settings manager requires a restart of the manager.
	  */
	 public synchronized void startAgentSettingsManager()
	 {
		 if (smtpAgentConfig == null || smtpAgent == null)
			 throw new IllegalStateException("Agent config and settings must be set first.");
		 
		 if (settingsManager != null)
			 throw new IllegalStateException("Settings manager is already running.");
		 
		 settingsManager = new SettingsManager(smtpAgent, smtpAgentConfig, settingsThreadDelay);
		 
		 // JDK documentation suggests it is a better practice to use a thread factory instead of 
		 // an ExeutorService to create and manage long running daemon threads
		 Thread managerThread = Executors.defaultThreadFactory().newThread(settingsManager);
		 managerThread.setDaemon(true);
		 managerThread.setName("SMTP Gateway State Update Thread");
		 managerThread.start();
	 }
	 
	 /**
	  * Stops the agent settings manager.
	  */
	 public synchronized void stopAgentSettingsManager()
	 {
		 if (settingsManager == null)
			 throw new IllegalStateException("Settings manager is not running.");
		 
		 synchronized(settingsManager)
		 {
			 // shutdown the settings manage and notify it to wake up and shutdown
			 settingsManager.setRunning(false);
			 settingsManager.notifyAll();
		 }
		 
		 settingsManager = null;
	 }
	 
	 /**
	  * Determines if the settings manager is running.
	  * @return True if the settings manage is running.  False otherwise.
	  */
	 public synchronized boolean isAgentSettingManagerRunning()
	 {
		 return (settingsManager != null);
	 }
	 
	 /**
	  * Manages the settings of the agent.
	  */
	 protected static class SettingsManager implements Runnable
	 {
		 private final SmtpAgent agent;
		 private final SmtpAgentConfig config;
		 private final long waitInterval;
		 private boolean isRunning = true;
		 
		 /**
		  * Constructor 
		  * @param agent The agent that whose settings will be managed.
		  * @param config The config object used to object the agent's settings and configuration.
		  * @param waitInterval The interval between each check of the agent's settings.
		  */
		 public SettingsManager(SmtpAgent agent, SmtpAgentConfig config, long waitInterval)
		 {
			 this.agent = agent;
			 this.config = config;
			 this.waitInterval = waitInterval;
		 }
		 
		 /**
		  * {@inheritDoc}}
		  */
		 public void run()
		 {
			 
			final NHINDAgent theAgent = agent.getAgent();
			
			// make sure the agent is mutable before trying to update it
			if (!(theAgent instanceof MutableAgent))
			{
				LOGGER.warn("The configured agent is not mutable.  Configuration changes cannot be applied.");
				return;
			}
			final MutableAgent runningAgent = (MutableAgent)theAgent;
			
			while(isRunning())
			{	
				NHINDAgent newAgent = null;
				
				// build a new configuration
				try
				{
					LOGGER.info("Refreshing agent settings from configuration.");
					final Injector injector = config.getAgentInjector();
					newAgent = injector.getInstance(NHINDAgent.class);
					if (!(newAgent instanceof MutableAgent))
					{
						LOGGER.warn("The agent configuration does not allow attributes to be retrieved.  Cannot update currently running agent.");
						return;
					}
				}
				catch (Throwable t)
				{
					LOGGER.warn("Could not get new agent settings.  Configuration may be in an invalid state or not reachable.", t);
					continue;
				}
					
				final MutableAgent newMutableAgent = (MutableAgent)newAgent;
				
				// lock the system to stop new messages from flowing
				GatewayState.INSTANCE.lockForUpdating();
				try
				{
					// set the attributes of the agent
					runningAgent.setDomains(newMutableAgent.getDomains());
					runningAgent.setTrustAnchorResolver(newMutableAgent.getTrustAnchors());
					runningAgent.setCryptographer(newMutableAgent.getCryptographer());
					runningAgent.setPrivateCertResolver(newMutableAgent.getPrivateCertResolver());
					runningAgent.setPublicCertResolvers(newMutableAgent.getPublicCertResolvers());
					
				}
				finally
				{
					// release the update lock
					GatewayState.INSTANCE.unlockFromUpdating();
				}
				
				synchronized(this)
				{
					try
					{
						if (isRunning())
						// wait for the configured interval
							this.wait(waitInterval);
					}
					catch (InterruptedException e) {/*no-op*/}
				}
			}
		 }
		 
		 /**
		  * Sets the running flag of the manager. 
		  * @param running 
		  */
		 public synchronized void setRunning(boolean running)
		 {
			 this.isRunning = running;
		 }
		 
		 /**
		  * Indicates if the manager is running.
		  * @return True if the manager is running.  False otherwise.
		  */
		 public synchronized boolean isRunning()
		 {
			 return isRunning;
		 }
	 }
}
