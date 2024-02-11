import { Accordion, AccordionDetails, AccordionSummary, Button, Card, CardContent, Divider, Stack, TextField, Typography } from "@mui/material";
import { CategoryDto, ConditionDto } from "../../api/dtos"
import { useEffect, useRef, useState } from "react";
import { MuiColorInput } from "mui-color-input";
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ConditionForm from "../condition-form/ConditionForm";
import { createCategory } from "../../api/bank-account-data-api";

type Props = {
    category?: CategoryDto;
    discard: () => void;
};

const CategoryForm = ({ category, discard }: Props) => {
    const [loading, setLoading] = useState(false);
    const [wantToAdd, setWantToAdd] = useState(false)
    const [conditions, setConditions] = useState(category?.conditions || []);
    const [colorCode, setColorCode] = useState(category?.colorCode || 'rgb(255, 255, 255)');
    const nameRef = useRef<HTMLInputElement>();

    const changeColor = (color: string) => {
        setColorCode(color);
    };

    const addCondition = (condition: ConditionDto) => {
        setConditions([...conditions, condition]);
        setWantToAdd(false);
    };

    const removeCondition = (index: number) => {
        if (index < conditions.length) {
            let newConditions = [...conditions.slice(0, index), ...conditions.slice(index + 1)];
            setConditions(newConditions);
        }
        else {
            setWantToAdd(false);
        }
    };

    const saveCategory = (event: React.FormEvent) => {
        event.preventDefault();

        if (nameRef.current && nameRef.current.value && colorCode && !wantToAdd) {
            setLoading(true);

            createCategory({
                id: category?.id,
                name: nameRef.current.value,
                colorCode: colorCode,
                conditions: conditions
            }).then(() => {
                setLoading(false);
                discard();
            });
        }
    };

    return (
        <Stack sx={{ height: '95%' }}>
            <Stack sx={{ height: '92%' }} direction="row" spacing={5} divider={<Divider variant="middle" orientation="vertical" flexItem />}>
                <Stack sx={{ width: '40%' }} spacing={3}>
                    <Typography sx={{ alignSelf: 'center' }} variant="h5">Category details</Typography>
                    <TextField label="Name" required fullWidth defaultValue={category?.name} inputRef={nameRef} />
                    <MuiColorInput label='Color code' value={colorCode} onChange={changeColor} required fullWidth />
                </Stack>
                <Stack sx={{ height: '100%', width: '100%' }} spacing={3}>
                    <Typography sx={{ textAlignLast: 'center' }} variant="h5">Conditions</Typography>

                    <div style={{ overflow: 'auto' }}>
                        {conditions.map((condition, index) => (
                            <Card key={condition.id || index} sx={{ marginBottom: 2 }}>
                                <CardContent>
                                    <ConditionForm condition={condition} index={index} addCondition={addCondition} removeCondition={removeCondition} />
                                </CardContent>
                            </Card>
                        ))}

                        {conditions.length === 0 &&
                            <Card>
                                <CardContent>
                                    <ConditionForm addCondition={addCondition} removeCondition={removeCondition} />
                                </CardContent>
                            </Card>
                        }
                        {conditions.length !== 0 && !wantToAdd && <Button variant="text" onClick={() => setWantToAdd(true)}>Add condition</Button>}
                        {conditions.length !== 0 && wantToAdd &&
                            <Card>
                                <CardContent>
                                    <ConditionForm index={conditions.length} addCondition={addCondition} removeCondition={removeCondition} />
                                </CardContent>
                            </Card>
                        }
                    </div>
                </Stack>
            </Stack>

            <Stack sx={{ display: 'block', marginLeft: 'auto', marginTop: 3 }} direction="row" spacing={1}>
                <Button variant="contained" onClick={saveCategory} disabled={loading}>Save</Button>
                <Button variant="outlined" onClick={discard}>Discard</Button>
            </Stack>
        </Stack>
    );
};

export default CategoryForm;