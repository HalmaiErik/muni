export type CreateRequisitionRequest = {
    institutionId: string;
    redirectUrl: string;
};

export type CreateCustomerRequest = {
    requisitionId: string;
    institutionName: string;
    institutionLogo: string;
};

export type AccountFullInfoRequest = {
    accountExternalId: string;
    refresh: boolean;
}