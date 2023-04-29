import { IconApps, IconCloudComputing, IconDatabaseExport, IconDatabaseImport, IconHome, IconTransform, IconUsers } from '@tabler/icons-react';

const navItems = [
    {
        key: "home",
        label: "Home",
        url: "/home",
        Icon: IconHome
    },
    {
        key: "pipelines",
        label: "Pipelines",
        url: "/pipelines",
        Icon: IconCloudComputing
    },
    {
        key: "sources",
        label: "Sources",
        url: "/sources",
        Icon: IconDatabaseExport
    },
    {
        key: "destinations",
        label: "Destinations",
        url: "/destinations",
        Icon: IconDatabaseImport
    },
    {
        key: "transformations",
        label: "Transformations",
        url: "/transformations",
        Icon: IconTransform
    },
    {
        key: "users",
        label: "Users",
        url: "/users",
        Icon: IconUsers
    },
    {
        key: "apps",
        label: "Apps",
        url: "/apps",
        Icon: IconApps
    }
];

export { navItems };