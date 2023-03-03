import { IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconHome, IconTransform } from '@tabler/icons-react';

const navItems = [
    {
        key: "home",
        label: "Home",
        url: "/home",
        icon: () => <IconHome size={24} />
    },
    {
        key: "pipelines",
        label: "Data Pipelines",
        url: "/pipelines",
        icon: () => <IconCloudComputing size={24} />
    },
    {
        key: "sources",
        label: "Data Sources",
        url: "/sources",
        icon: () => <IconDatabaseExport size={24} />
    },
    {
        key: "destinations",
        label: "Data Destinations",
        url: "/destinations",
        icon: () => <IconDatabaseImport size={24} />
    },
    {
        key: "transformations",
        label: "Transformations",
        url: "/transformations",
        icon: () => <IconTransform size={24} />
    }
];

export { navItems };