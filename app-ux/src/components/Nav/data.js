import { IconCloudDownload, IconCloudUpload, IconHome, IconTransform } from '@tabler/icons-react';

const navItems = [
    {
        key: "home",
        label: "Home",
        url: "/home",
        icon: () => <IconHome width={24} />
    },
    {
        key: "sources",
        label: "Sources",
        url: "/sources",
        icon: () => <IconCloudDownload width={24} />
    },
    {
        key: "destinations",
        label: "Destinations",
        url: "/destinations",
        icon: () => <IconCloudUpload width={24} />
    },
    {
        key: "transformations",
        label: "Transformations",
        url: "/transformations",
        icon: () => <IconTransform width={24} />
    }
];

export { navItems };