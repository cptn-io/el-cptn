const Tabs = (props) => {
    const { tabs, activeTab, onTabChange } = props;
    if (!activeTab) {
        activeTab = tabs?.[0]?.key;
    }
    const changeTab = (tabKey) => {
        if (onTabChange) {
            onTabChange(tabKey);
        }
    }
    return <div className="tabs bg-base-200 mb-4 rounded">
        {tabs.map(tab => {
            return <a key={tab.key} className={`tab tab-lg tab-lifted ${tab.key === activeTab && 'tab-active'}`} onClick={() => changeTab(tab.key)}>{tab.label}</a>
        })}
    </div>
}

export default Tabs;