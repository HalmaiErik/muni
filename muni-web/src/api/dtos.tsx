export type InstitutionDto = {
    id: string;
    name: string;
    logo: string;
};

export type RequisitionDto = {
    id: string;
    link: string;
};

export type AccountDto = {
    externalId: string;
    name: string;
    iban: string;
    currency: string;
    status: 'ACTIVE' | 'EXPIRED';
    institutionName: string;
    institutionLogo: string;
}

export type TransactionDto = {
    externalId: string;
    refFromInstitution: string;
    amount: number;
    bookingDate: Date;
    remittanceInfo: string;
}

export type AccountFullInfoDto = {
    account: AccountDto;
    transactions: TransactionDto[];
}