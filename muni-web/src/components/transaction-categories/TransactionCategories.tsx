import { Button, Card, Checkbox, FormControlLabel, IconButton, Popover, Tooltip } from "@mui/material";
import { CategoryDto, TransactionCategoryDto, TransactionDto } from "../../api/dtos";
import AddIcon from '@mui/icons-material/Add';
import { useState } from "react";
import { useEditTransactionCategories } from "../../api/bank-account-data-api";

type Props = {
    transaction: TransactionDto;
    customerCategories: CategoryDto[];
};

const TransactionCategories = ({ transaction, customerCategories }: Props) => {
    const [categoriesPopover, setCategoriesPopover] = useState<HTMLButtonElement | null>(null);
    const { mutate: editTransactionCategories } = useEditTransactionCategories();

    let selectedCatergories: number[] = transaction.categories.map(category => category.id);

    const popoverOpen = Boolean(categoriesPopover);

    const handleOpenPopover = (event: React.MouseEvent<HTMLButtonElement>) => {
        setCategoriesPopover(event?.currentTarget);
    };

    const handleClosePopover = () => {
        setCategoriesPopover(null);
    };

    const isTransactionOfCategory = (id?: number) => {
        if (id) {
            return transaction.categories.map(category => category.id).includes(id);
        }

        return false;
    };

    const editCategories = () => {
        editTransactionCategories({
            transactionExternalId: transaction.externalId,
            categoryIds: selectedCatergories
        })
    };

    const onChangeCheckbox = (isChecked: boolean, id?: number) => {
        if (!id) {
            return;
        }

        if (isChecked) {
            selectedCatergories.push(id);
        }
        else {
            const index = selectedCatergories.findIndex(selectedId => selectedId === id)
            selectedCatergories = [...selectedCatergories.slice(0, index), ...selectedCatergories.slice(index + 1)];
        }

        console.log(selectedCatergories);
    };

    return (
        <>
            <div style={{ display: 'flex', flexWrap: 'wrap', alignItems: 'center' }}>
                {transaction.categories.map((category) => (
                    <Tooltip key={category.id} title={category.name}>
                        <Card sx={{ height: '10px', width: '10px', backgroundColor: `${category.colorCode}`, margin: '2px' }} />
                    </Tooltip>
                ))}

                <Tooltip title="Add category">
                    <IconButton size="small" onClick={(handleOpenPopover)}>
                        <AddIcon fontSize="inherit" />
                    </IconButton>
                </Tooltip>
            </div>

            <Popover
                id={transaction.externalId}
                open={popoverOpen}
                anchorEl={categoriesPopover}
                onClose={handleClosePopover}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
            >
                <div style={{ padding: '8px', alignItems: 'center' }}>
                    {customerCategories.map((category) => (
                        <div key={category.id}>
                            <FormControlLabel
                                label={category.name}
                                control={
                                    <Checkbox defaultChecked={isTransactionOfCategory(category.id)} onChange={e => onChangeCheckbox(e.target.checked, category.id)} />
                                }
                            />
                        </div>
                    ))}
                    <Button sx={{ marginRight: '8px' }} variant="contained" size="small" onClick={editCategories}>Save</Button>
                    <Button variant="outlined" size="small">Discard</Button>
                </div>
            </Popover>
        </>
    )
};

export default TransactionCategories;