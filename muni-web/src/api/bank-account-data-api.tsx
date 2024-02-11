import { useMutation, useQuery, useQueryClient } from "react-query";
import { LOCAL_STORAGE_TOKEN_KEY } from "../utils/constants";
import { AccountDto, RequisitionDto, InstitutionDto, TransactionDto, AccountFullInfoDto, CategoryDto } from "./dtos";
import { CreateCustomerRequest, CreateRequisitionRequest, AccountFullInfoRequest, CategorizeAccountTransactionsRequest } from "./requests";
import { User } from "firebase/auth";

const baseUrl = 'http://localhost:8080/api/v1/bankaccount';

const getCountryInstitutions = async (countryCode: string) => {
    const response = await fetch(baseUrl + '/institutions/' + countryCode, {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`
        }),
    });
    const data: InstitutionDto[] = await response.json();
    return data;
};

const createRequisition = async (request: CreateRequisitionRequest) => {
    const response = await fetch(baseUrl + '/requisitions', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
    const data: RequisitionDto = await response.json();
    return data;
};

const createCustomer = async (request: CreateCustomerRequest) => {
    await fetch(baseUrl + '/customer/create', {
        method: 'post',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
            'Content-Type': 'application/json'
        }),
        body: JSON.stringify(request)
    });
};

const getCustomerAccounts = async () => {
    const response = await fetch(baseUrl + '/accounts', {
        method: 'get',
        headers: new Headers({
            'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
        }),
    });
    const data: AccountDto[] = await response.json();
    return data;
};

const useAccountFullInfo = (refresh: boolean, currentUser: User | null, accountExternalId?: string) => {
    return useQuery(
        'accountFullInfo',
        async () => {
            const response = await fetch(baseUrl + '/account', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify({ accountExternalId, refresh })
            });
            const data: AccountFullInfoDto = await response.json();
            return data;
        },
        { enabled: !!accountExternalId && !!currentUser }
    );
};

const useCreateCategory = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (category: CategoryDto) => {
            await fetch(baseUrl + '/category/create', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(category)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] })
        }
    });
};

const useCategorizeAccountTransactions = () => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (request: CategorizeAccountTransactionsRequest) => {
            await fetch(baseUrl + '/categorize/account', {
                method: 'post',
                headers: new Headers({
                    'Authorization': `Bearer ${window.localStorage.getItem(LOCAL_STORAGE_TOKEN_KEY)}`,
                    'Content-Type': 'application/json'
                }),
                body: JSON.stringify(request)
            });
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['accountFullInfo'] })
        }
    });
};

export {
    getCountryInstitutions,
    createRequisition,
    createCustomer,
    getCustomerAccounts,
    useAccountFullInfo,
    useCreateCategory,
    useCategorizeAccountTransactions
};