package org.init.beans.factory.support;


import org.init.beans.BeansException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    protected List<String> beanNames=new ArrayList<>();
    protected Map<String, Object> singletonObjects =new ConcurrentHashMap<>(256);
	protected Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);
	protected Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    protected Map<String,Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);
    protected Map<String,Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);
	private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap(16));

	@Override
	public void registerSingleton(String beanName, Object singletonObject) {
		synchronized(this.singletonObjects) {
			Object oldObject = this.singletonObjects.get(beanName);
			if (oldObject != null) {
				throw new IllegalStateException("Could not register object [" + singletonObject +
						"] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			}

			this.singletonObjects.put(beanName, singletonObject);
			this.beanNames.add(beanName);
			System.out.println(" bean registerded............. " + beanName);
		}
	}

	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) throws BeansException {
		synchronized(this.singletonObjects) {
			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null) {
				this.beforeSingletonCreation(beanName);
				boolean newSingleton = false;
				try {
					singletonObject = singletonFactory.getObject();
					newSingleton = true;
				}catch (IllegalStateException | BeansException e){
					singletonObject = this.singletonObjects.get(beanName);
					if (singletonObject == null) {
						throw e;
					}
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				} finally {
					this.afterSingletonCreation(beanName);
				}
				if (newSingleton) {
					this.singletonObjects.put(beanName, singletonObject);
					this.singletonFactories.remove(beanName);
					this.earlySingletonObjects.remove(beanName);
				}
			}
			return singletonObject;
		}
	}
	public boolean isCurrentlyInCreation(String beanName) {
		return  this.singletonsCurrentlyInCreation.contains(beanName);
	}
	protected void afterSingletonCreation(String beanName) {
		this.singletonsCurrentlyInCreation.remove(beanName);
	}

	protected void beforeSingletonCreation(String beanName) {
		this.singletonsCurrentlyInCreation.add(beanName);
	}

	@Override
		public Object getSingleton(String beanName) {

			Object singletonObject = this.singletonObjects.get(beanName);
			if (singletonObject == null && this.isSingletonCurrentlyInCreation(beanName)) {
				synchronized(this.singletonObjects) {
					singletonObject = this.earlySingletonObjects.get(beanName);
					if (singletonObject == null) {
						ObjectFactory<?> singletonFactory = (ObjectFactory)this.singletonFactories.get(beanName);
						if (singletonFactory != null) {
							try {
								singletonObject = singletonFactory.getObject();
							} catch (BeansException e) {
								throw new RuntimeException(e);
							} catch (ClassNotFoundException e) {
								throw new RuntimeException(e);
							}
							this.earlySingletonObjects.put(beanName, singletonObject);
							this.singletonFactories.remove(beanName);
						}
					}
				}
			}
			return singletonObject;
		}

		public boolean isSingletonCurrentlyInCreation(String beanName) {
			return this.singletonsCurrentlyInCreation.contains(beanName);
		}

		@Override
		public boolean containsSingleton(String beanName) {
			return this.singletonObjects.containsKey(beanName);
		}

		@Override
		public String[] getSingletonNames() {
			return (String[]) this.beanNames.toArray();
		}

		public void removeSingleton(String beanName) {
			synchronized (this.singletonObjects) {
				this.singletonObjects.remove(beanName);
				this.beanNames.remove(beanName);
			}
		}

		public void registerDependentBean(String beanName, String dependentBeanName) {
			Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
			if (dependentBeans != null && dependentBeans.contains(dependentBeanName)) {
				return;
			}

			synchronized (this.dependentBeanMap) {
				dependentBeans = this.dependentBeanMap.get(beanName);
				if (dependentBeans == null) {
					dependentBeans = new LinkedHashSet<String>(8);
					this.dependentBeanMap.put(beanName, dependentBeans);
				}
				dependentBeans.add(dependentBeanName);
			}
			synchronized (this.dependenciesForBeanMap) {
				Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(dependentBeanName);
				if (dependenciesForBean == null) {
					dependenciesForBean = new LinkedHashSet<String>(8);
					this.dependenciesForBeanMap.put(dependentBeanName, dependenciesForBean);
				}
				dependenciesForBean.add(beanName);
			}

		}

		public boolean hasDependentBean(String beanName) {
			return this.dependentBeanMap.containsKey(beanName);
		}
		public String[] getDependentBeans(String beanName) {
			Set<String> dependentBeans = this.dependentBeanMap.get(beanName);
			if (dependentBeans == null) {
				return new String[0];
			}
			return (String[]) dependentBeans.toArray();
		}
		public String[] getDependenciesForBean(String beanName) {
			Set<String> dependenciesForBean = this.dependenciesForBeanMap.get(beanName);
			if (dependenciesForBean == null) {
				return new String[0];
			}
			return (String[]) dependenciesForBean.toArray();

		}
}
