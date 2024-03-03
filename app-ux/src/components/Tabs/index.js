const Tabs = (props) => {
    const { tabs, activeTab, onTabChange } = props;

    const changeTab = (tabKey) => {
        if (onTabChange) {
            onTabChange(tabKey);
        }
    }
    return <div role="tablist" className="tabs bg-base-200 rounded tabs-md md:tabs-lg tabs-lifted">
        {tabs.map(tab => {
            return <button type="button" role="tab" key={tab.key} className={`tab  ${tab.key === (activeTab || tabs?.[0]?.key) ? 'tab-active' : ''}`} onClick={() => changeTab(tab.key)} aria-label={tab.label}>{tab.label}</button>
        })}
    </div>
}

export default Tabs;