import { ArrowPathRoundedSquareIcon, HomeIcon, CloudArrowDownIcon, CloudArrowUpIcon } from '@heroicons/react/24/solid'

const navItems = [
    {
        key: "home",
        label: "Home",
        url: "/app/home",
        icon: () => <HomeIcon className="h-6 w-6" />
    },
    {
        key: "sources",
        label: "Sources",
        url: "/app/sources",
        icon: () => <CloudArrowDownIcon className="h-6 w-6" />
    },
    {
        key: "destinations",
        label: "Destinations",
        url: "/app/destinations",
        icon: () => <CloudArrowUpIcon className="h-6 w-6" />
    },
    {
        key: "transformations",
        label: "Transformations",
        url: "/app/transformations",
        icon: () => <ArrowPathRoundedSquareIcon className="h-6 w-6" />
    }
];

export { navItems };